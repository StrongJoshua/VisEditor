/*
 * Copyright 2014-2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.util.Log;

public class RendererModule extends SceneModule {
	private CameraModule camera;
	private ShapeRenderer shapeRenderer;
	private ShaderProgram fontShader;

	@Override
	public void added () {
		shapeRenderer = new ShapeRenderer();

		fontShader = new ShaderProgram(Gdx.files.internal("shader/bmp-font-df.vert"), Gdx.files.internal("shader/bmp-font-df.frag"));
		if (!fontShader.isCompiled()) {
			Log.fatal("Renderer", "FontShader compilation failed:\n" + fontShader.getLog());
			throw new IllegalStateException("Shader compilation failed");
		}
	}

	@Override
	public void init () {
		camera = sceneContainer.get(CameraModule.class);
	}

	@Override
	public void render (Batch batch) {
		boolean useShader;

		for (EditorObject entity : scene.entities) {
			useShader = false;

			if (entity instanceof TextObject) {
				TextObject obj = (TextObject) entity;
				if (obj.isDistanceFieldShaderEnabled()) useShader = true;
			}

			if (useShader) batch.setShader(fontShader);
			entity.render(batch);
			if (useShader) batch.setShader(null);
		}

		batch.end();
		shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.rect(0, 0, scene.width, scene.height);
		shapeRenderer.end();
		batch.begin();
	}

	@Override
	public void dispose () {
		shapeRenderer.dispose();
	}

	public ShapeRenderer getShapeRenderer () {
		return shapeRenderer;
	}
}
