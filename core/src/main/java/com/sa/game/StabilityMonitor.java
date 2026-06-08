package com.sa.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.sa.game.assets.Assets;
import com.sa.game.assets.TextureType;
import com.sa.game.blocks.Block;
import com.sa.game.physics.PhysicsProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StabilityMonitor implements Disposable {


    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }

    static class StabilityData {
        Block blockA;
        Block blockB;
        float currentForce;     // Impulso atual (não suavizado)
        float smoothedForce;    // Força suavizada para evitar variações abruptas
        float maxSafeForce;
        RiskLevel riskLevel;
        float timeAtHighRisk = 0f; // Acumulador de tempo para risco alto
        Vector3 forceDirection = new Vector3(); // Contact normal direction
    }

    private final List<StabilityData> monitoredJoints = new ArrayList<>();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private boolean renderForces = false;

    public void registerConnection(Block blockA, Block blockB) {

        MaterialType materialType = blockA.getMaterialType(); // ou usar o mais fraco dos dois

        float area = PhysicsProperties.getContactArea(blockA, blockB); // m²
        float strength = PhysicsProperties.getCompressiveStrength(materialType); // N/m²

        float safetyFactor = 10000f;
        float maxSafeForce = (area * strength) / safetyFactor; // N (newtons) / 10000

        StabilityData data = new StabilityData();
        data.blockA = blockA;
        data.blockB = blockB;
        data.maxSafeForce = maxSafeForce;
        data.currentForce = 0;
        data.smoothedForce = 0;
        data.riskLevel = RiskLevel.LOW;
        monitoredJoints.add(data);
    }

    public void update(float deltaTime, btDispatcher dispatcher) {
        for (StabilityData data : monitoredJoints) {
            float maxImpulse = 0;

            // Percorre todos os contatos para encontrar forças entre os blocos
            int numManifolds = dispatcher.getNumManifolds();
            for (int i = 0; i < numManifolds; i++) {
                btPersistentManifold manifold = dispatcher.getManifoldByIndexInternal(i);

                UUID body0Id = (UUID) manifold.getBody0().userData;
                UUID body1Id = (UUID) manifold.getBody1().userData;

                if ((body0Id == data.blockA.getBody().userData && body1Id == data.blockB.getBody().userData)
                    || (body0Id == data.blockB.getBody().userData && body1Id == data.blockA.getBody().userData)) {

                    for (int j = 0; j < manifold.getNumContacts(); j++) {
                        btManifoldPoint pt = manifold.getContactPoint(j);
                        maxImpulse = Math.max(maxImpulse, pt.getAppliedImpulse());

                        // Store contact normal as force direction
                        Vector3 normal = new Vector3();
                        pt.getNormalWorldOnB(normal);
                        data.forceDirection.set(normal);
                    }
                }
            }

            data.currentForce = maxImpulse;

            // Aplica interpolação exponencial para suavizar as variações
            float alpha = 1.0f - (float)Math.exp(-5 * deltaTime); // fator de suavização
            data.smoothedForce += alpha * (data.currentForce - data.smoothedForce);

            data.riskLevel = calculateRisk(data.smoothedForce, data.maxSafeForce);

            // Apply color to both blocks based on force
            applyForceColor(data.blockA, data);
            applyForceColor(data.blockB, data);

            if (data.riskLevel == RiskLevel.HIGH) {
                data.timeAtHighRisk += deltaTime;
                applyVibrationEffect(data.blockA);
                applyVibrationEffect(data.blockB);
            } else {
                data.timeAtHighRisk = 0f;
            }
        }
    }

    public void renderForces(Camera camera) {

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (StabilityData data : monitoredJoints) {
            Vector3 posA = new Vector3(data.blockA.getModelInstance().transform.getTranslation(new Vector3()));
            Vector3 posB = new Vector3(data.blockB.getModelInstance().transform.getTranslation(new Vector3()));
            Vector3 midpoint = new Vector3(posA).add(posB).scl(0.5f);

            // Force magnitude (normalized for visualization)
            float forceScale = Math.min(data.smoothedForce / data.maxSafeForce, 2f);

            // Color based on risk level
            Color color = switch (data.riskLevel) {
                case LOW -> Color.GREEN;
                case MEDIUM -> Color.YELLOW;
                case HIGH -> Color.RED;
            };

            shapeRenderer.setColor(color);

            // Draw force arrow
            Vector3 forceEndpoint = new Vector3(midpoint).mulAdd(data.forceDirection, forceScale);
            shapeRenderer.line(midpoint, forceEndpoint);

            // Draw arrowhead
            Vector3 arrowBase = new Vector3(forceEndpoint).mulAdd(data.forceDirection, -0.3f);
            Vector3 perpendicular = new Vector3(-data.forceDirection.z, 0, data.forceDirection.x).nor().scl(0.2f);
            shapeRenderer.line(forceEndpoint, new Vector3(arrowBase).add(perpendicular));
            shapeRenderer.line(forceEndpoint, new Vector3(arrowBase).sub(perpendicular));

            // Draw force value as text (optional - render separately with SpriteBatch)
/*            System.out.printf("Force: %.2f / %.2f (%.1f%%)%n",
                data.smoothedForce, data.maxSafeForce,
                (data.smoothedForce / data.maxSafeForce) * 100);*/
        }

        shapeRenderer.end();
    }

    private void applyForceColor(Block block, StabilityData data) {

        if(renderForces) {
            float ratio = Math.min(data.smoothedForce / data.maxSafeForce, 1f);

            // Heatmap: Blue -> Cyan -> Green -> Yellow -> Red
            Color forceColor;
            if (ratio < 0.25f) {
                float t = ratio / 0.25f;
                forceColor = new Color(0f, t, 1f, 0.8f); // Blue to Cyan
            } else if (ratio < 0.5f) {
                float t = (ratio - 0.25f) / 0.25f;
                forceColor = new Color(0f, 1f, 1f - t, 0.8f); // Cyan to Green
            } else if (ratio < 0.75f) {
                float t = (ratio - 0.5f) / 0.25f;
                forceColor = new Color(t, 1f, 0f, 0.8f); // Green to Yellow
            } else {
                float t = (ratio - 0.75f) / 0.25f;
                forceColor = new Color(1f, 1f - t, 0f, 0.8f); // Yellow to Red
            }

            block.getModelInstance().materials.get(0).set(ColorAttribute.createDiffuse(forceColor));

        } else {
            revertBlockMaterial(block);
        }

    }

    private void revertBlockMaterial(Block block) {
        var material = block.getModelInstance().materials.get(0);
        if(material.has(ColorAttribute.Diffuse)) {
            material.remove(ColorAttribute.Diffuse);
        }
    }

    public void setRenderForces(boolean render) {
        this.renderForces = render;
    }

    public boolean isRenderForces() {
        return renderForces;
    }

    public void renderWarnings(SpriteBatch batch, Camera camera) {
        batch.begin();
        for (StabilityData data : monitoredJoints) {
            Block block = data.blockA; // usa um dos blocos como referência
            Vector3 position = new Vector3(block.getModelInstance().transform.getTranslation(new Vector3()));
            Vector3 screenPos = camera.project(position);

            Texture icon;
            switch (data.riskLevel) {
                case LOW: icon = Assets.getInstance().get(TextureType.WARNING_GREEN); break;
                case MEDIUM: icon = Assets.getInstance().get(TextureType.WARNING_YELLOW); break;
                case HIGH: icon = Assets.getInstance().get(TextureType.WARNING_RED); break;
                default: continue;
            }

            batch.draw(icon, screenPos.x, screenPos.y);
        }
        batch.end();
    }

    private RiskLevel calculateRisk(float current, float max) {
        float ratio = current / max;
        if (ratio < 0.3f) return RiskLevel.LOW;
        else if (ratio < 0.7f) return RiskLevel.MEDIUM;
        else return RiskLevel.HIGH;
    }

    private void applyVibrationEffect(Block block) {
        float vibration = MathUtils.sin(TimeUtils.millis() / 30f) * 0.05f;
        block.getModelInstance().transform.translate(0, vibration, 0);
    }

    public void removeConnection(Block blockRemoved) {
        monitoredJoints.removeIf(stabilityData ->  stabilityData.blockA.getId().equals(blockRemoved.getId()) ||  stabilityData.blockB.getId().equals(blockRemoved.getId()));
    }

    public void clear() {
        renderForces = false;
        monitoredJoints.clear();
    }

    @Override
    public void dispose() {
        monitoredJoints.clear();
        shapeRenderer.dispose();
    }
}
