package com.sa.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.sa.game.assets.Assets;
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
    }

    private final List<StabilityData> monitoredJoints = new ArrayList<>();

    public void registerConnection(Block blockA, Block blockB) {

        MaterialType material = blockA.getMaterial(); // ou usar o mais fraco dos dois

        float area = PhysicsProperties.getContactArea(blockA, blockB); // m²
        float strength = PhysicsProperties.getCompressiveStrength(material); // N/m²

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
                    }
                }
            }

            data.currentForce = maxImpulse;

            // Aplica interpolação exponencial para suavizar as variações
            float alpha = 1.0f - (float)Math.exp(-5 * deltaTime); // fator de suavização
            data.smoothedForce += alpha * (data.currentForce - data.smoothedForce);

            data.riskLevel = calculateRisk(data.smoothedForce, data.maxSafeForce);

            if (data.riskLevel == RiskLevel.HIGH) {
                data.timeAtHighRisk += deltaTime;
                applyVibrationEffect(data.blockA);
                applyVibrationEffect(data.blockB);
            } else {
                data.timeAtHighRisk = 0f;
            }
        }
    }

    public void renderWarnings(SpriteBatch batch, Camera camera) {
        batch.begin();
        for (StabilityData data : monitoredJoints) {
            Block block = data.blockA; // usa um dos blocos como referência
            Vector3 position = new Vector3(block.getModelInstance().transform.getTranslation(new Vector3()));
            Vector3 screenPos = camera.project(position);

            Texture icon;
            switch (data.riskLevel) {
                case LOW: icon = Assets.getInstance().warningGreen; break;
                case MEDIUM: icon = Assets.getInstance().warningYellow; break;
                case HIGH: icon = Assets.getInstance().warningRed; break;
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

    @Override
    public void dispose() {
        monitoredJoints.clear();
    }
}
