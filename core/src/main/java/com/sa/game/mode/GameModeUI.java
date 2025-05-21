package com.sa.game.mode;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
public class GameModeUI {
    private final Table rootTable;
    private final SelectBox<GameModeType> modeSelectBox;
    private final Label descriptionLabel;

    private final TextButton startButton;

    public GameModeUI(Skin skin, GameModeManager gameModeManager) {

        BitmapFont bigFont = skin.getFont("bigFont");

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();
        rootTable.pad(20);

        // Título
        Label title = new Label("Game Mode:", skin, "bigFont", Color.BLACK);
        rootTable.add(title).colspan(2).padBottom(16).row();

        // Criar um estilo customizado para a SelectBox
        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.font = bigFont;  // Substitua pelo seu estilo de fonte preferido
        selectBoxStyle.background = skin.getDrawable("selectBox"); // Certifique-se de que a imagem de fundo seja adequada
        selectBoxStyle.scrollStyle = skin.get("default", ScrollPane.ScrollPaneStyle.class);

        List.ListStyle style = new List.ListStyle();
        style.font = bigFont;
        style.background = skin.getDrawable("selectBox");
        style.selection = skin.getDrawable("selectBox");
        selectBoxStyle.listStyle = style;


        // SelectBox
        modeSelectBox = new SelectBox<>(selectBoxStyle);
        modeSelectBox.setItems(GameModeType.values());
        modeSelectBox.setSelected(GameModeType.CREATIVE);
        modeSelectBox.setWidth(600);

        // Descrição inicial
        descriptionLabel = new Label(modeSelectBox.getSelected().getDescription(), skin, "bigFont", Color.BLACK);
        descriptionLabel.setWrap(false);
        descriptionLabel.setAlignment(Align.center);

        // Listener de mudança
        modeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameModeType selected = modeSelectBox.getSelected();
                gameModeManager.setMode(selected.createInstance());
                descriptionLabel.setText(selected.getDescription());
                modeSelectBox.hideScrollPane();
            }
        });

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = bigFont;

        startButton = new TextButton("Start", textButtonStyle);
        // Layout
        rootTable.add(modeSelectBox).width(300).padBottom(200).row();
        rootTable.add(descriptionLabel).width(350).padBottom(20).row();
        rootTable.add(startButton).width(250).height(70);
    }

    public void setStartButtonListener(Runnable listener) {
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listener.run();
            }
        });
    }

    public Table getRoot() {
        return rootTable;
    }
    public GameModeType getSelectedMode() {
        return modeSelectBox.getSelected();
    }
}
