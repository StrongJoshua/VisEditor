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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.util.ExceptionUtils;
import com.kotcrab.vis.editor.util.gdx.VisChangeListener;
import com.kotcrab.vis.ui.widget.*;

public class ExceptionDialog extends VisWindow {
	public ExceptionDialog (Throwable cause) {
		this(null, cause);
	}

	public ExceptionDialog (String text, Throwable cause) {
		super("Exception Details");

		addCloseButton();
		closeOnEscape();
		setModal(true);

		VisTextButton copyButton = new VisTextButton("Copy");
		VisTextButton okButton = new VisTextButton("OK");
		VisLabel errorLabel = new VisLabel(ExceptionUtils.getStackTrace(cause));

		VisTable detailsTable = new VisTable(true);
		detailsTable.add("Details:").left().expand().padTop(6);
		detailsTable.add(copyButton);
		detailsTable.row();
		detailsTable.add(createScrollPane(errorLabel)).colspan(2).width(600).height(300);

		if (text != null) add(text).row();
		add(detailsTable).row();
		add(okButton).padBottom(3).padTop(3);

		okButton.addListener(new VisChangeListener((event, actor) -> fadeOut()));

		copyButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Gdx.app.getClipboard().setContents((errorLabel.getText().toString()));
				copyButton.setText("Copied");
			}
		});

		pack();
		centerWindow();
	}

	private VisScrollPane createScrollPane (Actor widget) {
		VisScrollPane scrollPane = new VisScrollPane(widget);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		return scrollPane;
	}
}
