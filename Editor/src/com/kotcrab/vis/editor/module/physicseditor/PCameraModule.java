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

package com.kotcrab.vis.editor.module.physicseditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.editor.util.gdx.CameraZoomController;

public class PCameraModule extends PhysicsEditorModule {
	private PhysicsEditorSettings settings;

	private OrthographicCamera camera;
	private CameraZoomController zoomController;

	private Vector3 unprojectVec;

	@Override
	public void added () {
		camera = new OrthographicCamera();
		unprojectVec = new Vector3();
		zoomController = new CameraZoomController(camera, unprojectVec);

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera.viewportWidth = w / 400;
		camera.viewportHeight = w / 400 * h / w;
		camera.position.set(0.5f, 0.5f, 0);
		camera.update();
	}

	@Override
	public void init () {
		settings = physicsContainer.get(PSettingsModule.class).getSettings();
	}

	@Override
	public void resize () {
		Vector3 oldPos = camera.position.cpy();
		camera.position.set(oldPos);
	}

	@Override
	public void render (Batch batch) {
		camera.update();
	}

	@Override
	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		return zoomController.zoomAroundPoint(x, y, amount);
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		return true;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (Gdx.input.isButtonPressed(Buttons.RIGHT)) pan(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
	}

	private void pan (float deltaX, float deltaY) {
		camera.position.x = camera.position.x - deltaX * camera.zoom / 400.0f;
		camera.position.y = camera.position.y + deltaY * camera.zoom / 400.0f;
	}

	public Matrix4 getCombinedMatrix () {
		return camera.combined;
	}

	public float getX () {
		return camera.position.x;
	}

	public float getY () {
		return camera.position.y;
	}

	public float getHeight () {
		return camera.viewportHeight * camera.zoom;
	}

	public float getWidth () {
		return camera.viewportWidth * camera.zoom;
	}

	public float getZoom () {
		return camera.zoom;
	}

	public Vector3 unproject (Vector3 vector) {
		return camera.unproject(vector);
	}

	public float getInputX () {
		unprojectVec.x = Gdx.input.getX();
		camera.unproject(unprojectVec);
		return unprojectVec.x;
	}

	public float getInputY () {
		unprojectVec.y = Gdx.input.getY();
		camera.unproject(unprojectVec);
		return unprojectVec.y;
	}

	public void setPosition (float x, float y) {
		camera.position.set(x, y, 0);
	}

	public OrthographicCamera getCamera () {
		return camera;
	}

	public Vector2 screenToWorld (float x, float y) {
		Vector3 v3 = new Vector3(x, y, 0);
		camera.unproject(v3);
		return new Vector2(v3.x, v3.y);
	}

	public Vector2 alignedScreenToWorld (float x, float y) {
		Vector2 p = screenToWorld(x, y);
		if (settings.isSnapToGridEnabled) {
			float gap = settings.gridGap;
			p.x = Math.round(p.x / gap) * gap;
			p.y = Math.round(p.y / gap) * gap;
		}
		return p;
	}
}
