/*
 * Spine Runtimes Software License
 * Version 2.3
 *
 * Copyright (c) 2013-2015, Esoteric Software
 * All rights reserved.
 *
 * You are granted a perpetual, non-exclusive, non-sublicensable and
 * non-transferable license to use, install, execute and perform the Spine
 * Runtimes Software (the "Software") and derivative works solely for personal
 * or internal use. Without the written permission of Esoteric Software (see
 * Section 2 of the Spine Software License Agreement), you may not (a) modify,
 * translate, adapt or otherwise create derivative works, improvements of the
 * Software or develop new applications using the Software or (b) remove,
 * delete, alter or obscure any trademarks or any copyright, trademark, patent
 * or other intellectual property or proprietary rights notices on or in the
 * Software, including any copy thereof. Redistributions in binary or source
 * form must include this license and terms.
 *
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.kotcrab.vis.plugin.spine;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.vis.editor.module.project.ExportModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.plugin.ObjectSupport;
import com.kotcrab.vis.editor.ui.scene.entityproperties.ContentItemProperties;
import com.kotcrab.vis.editor.ui.scene.entityproperties.SpecificObjectTable;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.gdx.VisDropSource;
import com.kotcrab.vis.plugin.spine.runtime.SpineData;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

@VisPlugin
public class SpineEditorSupport extends ObjectSupport<SpineData, SpineObject> {
	private SpineCacheModule spineCache;
	private FileAccessModule fileAccess;

	private SpineSerializer serializer;

	@Override
	public void bindModules (ProjectModuleContainer projectMC) {
		SceneIOModule sceneIOModule = projectMC.get(SceneIOModule.class);
		spineCache = projectMC.get(SpineCacheModule.class);
		fileAccess = projectMC.get(FileAccessModule.class);

		serializer = new SpineSerializer(sceneIOModule.getKryo(), spineCache);
	}

	@Override
	public SpecificObjectTable getUIPropertyTable () {
		return new SpineObjectTable();
	}

	@Override
	public Class<SpineObject> getObjectClass () {
		return SpineObject.class;
	}

	@Override
	public SpineData getEmptyData () {
		return new SpineData();
	}

	@Override
	public boolean isSupportedDirectory (String extension, String relativePath) {
		return relativePath.startsWith("spine/");
	}

	@Override
	public ContentItemProperties getContentItemProperties (String relativePath, String ext) {
		if (ext.equals("json"))
			return new ContentItemProperties("Spine Json Skeleton", true);

		if (ext.equals("skel"))
			return new ContentItemProperties("Spine Binary Skeleton", true);

		return null;
	}

	@Override
	public Source createDropSource (DragAndDrop dragAndDrop, FileItem item) {
		return new VisDropSource(dragAndDrop, item).defaultView("New Spine Animation \n (drop on scene to add)").disposeOnNullTarget()
				.setObjectProvider(() -> {
					FileHandle atlasFile = FileUtils.sibling(item.getFile(), "atlas");
					return new SpineObject(fileAccess.relativizeToAssetsFolder(atlasFile.path()), fileAccess.relativizeToAssetsFolder(item.getFile().path()),
							spineCache.get(atlasFile, item.getFile()));
				});
	}

	@Override
	public Serializer<SpineObject> getSerializer () {
		return serializer;
	}

	@Override
	public void export (ExportModule module, Array<EntityData> entities, SpineObject entity) {

	}

}
