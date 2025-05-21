package com.sa.game.ui;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
public class FontFactory {
    public static BitmapFont generateBigFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size; // Por exemplo, 64 para mobile
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}
