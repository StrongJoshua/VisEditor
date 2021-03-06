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

package com.kotcrab.vis.editor.module.physicseditor.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.ModuleInput;
import com.kotcrab.vis.editor.module.physicseditor.PCameraModule;
import com.kotcrab.vis.editor.module.physicseditor.PRigidBodiesScreen;
import com.kotcrab.vis.editor.module.physicseditor.PhysicsEditorSettings;
import com.kotcrab.vis.editor.module.physicseditor.models.RigidBodyModel;
import com.kotcrab.vis.editor.module.physicseditor.models.ShapeModel;

/** @author Aurelien Ribon, Kotcrab */
public class CreationInputProcessor implements ModuleInput {
	private PCameraModule cameraModule;
	private PRigidBodiesScreen screen;
	private PhysicsEditorSettings settings;
	private boolean touchDown = false;

	public CreationInputProcessor (PCameraModule cameraModule, PRigidBodiesScreen screen, PhysicsEditorSettings settings) {
		this.cameraModule = cameraModule;
		this.screen = screen;
		this.settings = settings;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		touchDown = button == Buttons.LEFT;
		if (!touchDown) return true;

		RigidBodyModel model = screen.getSelectedModel();
		if (model == null) return true;

		Array<ShapeModel> shapes = model.getShapes();
		ShapeModel lastShape = shapes.size == 0 ? null : shapes.get(shapes.size - 1);

		if (lastShape == null || lastShape.isClosed()) {
			ShapeModel.Type type = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? ShapeModel.Type.CIRCLE : ShapeModel.Type.POLYGON;
			lastShape = new ShapeModel(type);
			lastShape.getVertices().add(cameraModule.alignedScreenToWorld(x, y));
			shapes.add(lastShape);

		} else {
			Array<Vector2> vs = lastShape.getVertices();
			Vector2 np = screen.nearestPoint;
			ShapeModel.Type type = lastShape.getType();

			if (type == ShapeModel.Type.POLYGON && vs.size >= 3 && np == vs.get(0)) {
				lastShape.close();
				model.computePhysics(settings.polygonizer);
				screen.buildBody();
			} else if (type == ShapeModel.Type.CIRCLE) {
				vs.add(cameraModule.alignedScreenToWorld(x, y));
				lastShape.close();
				model.computePhysics(settings.polygonizer);
				screen.buildBody();
			} else {
				vs.add(cameraModule.alignedScreenToWorld(x, y));
			}
		}

		return true;
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		touchDown = false;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (!touchDown) return;
		mouseMoved(event, x, y);
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		RigidBodyModel model = screen.getSelectedModel();
		if (model == null) return false;

		// Nearest point computation

		screen.nearestPoint = null;
		Vector2 p = cameraModule.screenToWorld(x, y);

		Array<ShapeModel> shapes = model.getShapes();
		ShapeModel lastShape = shapes.size == 0 ? null : shapes.get(shapes.size - 1);

		if (lastShape != null) {
			Array<Vector2> vs = lastShape.getVertices();
			float zoom = cameraModule.getCamera().zoom;

			if (!lastShape.isClosed() && vs.size >= 3)
				if (vs.get(0).dst(p) < 0.025f * zoom)
					screen.nearestPoint = vs.get(0);
		}

		// Next point assignment

		screen.nextPoint = cameraModule.alignedScreenToWorld(x, y);
		return false;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		switch (keycode) {
			case Input.Keys.ESCAPE:
				RigidBodyModel model = screen.getSelectedModel();
				if (model == null) break;
				if (model.getShapes().size == 0) break;
				if (model.getShapes().get(model.getShapes().size - 1).isClosed()) break;
				model.getShapes().removeIndex(model.getShapes().size - 1);
				break;
		}
		return false;
	}
}
