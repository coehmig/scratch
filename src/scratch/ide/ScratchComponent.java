/*
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
package scratch.ide;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;
import scratch.MrScratchManager;
import scratch.ScratchConfig;
import scratch.filesystem.FileSystem;

import static java.util.Arrays.asList;

public class ScratchComponent implements ApplicationComponent {

	private MrScratchManager mrScratchManager;

	public static MrScratchManager mrScratchManager() {
		return ApplicationManager.getApplication().getComponent(ScratchComponent.class).mrScratchManager;
	}

	@Override
	public void initComponent() {
		FileSystem fileSystem = new FileSystem();
		ScratchLog log = new ScratchLog();
		Ide ide = new Ide(fileSystem, log);
		ScratchConfig config = ScratchConfigPersistence.getInstance().asConfig();

		mrScratchManager = new MrScratchManager(ide, fileSystem, config, log);

		if (config.needMigration) {
			ScratchOldData scratchOldData = ScratchOldData.getInstance();
			mrScratchManager.migrate(asList(scratchOldData.getScratchTextInternal()));
		}

		new Ide.ClipboardListener(mrScratchManager).startListening();
	}

	@NotNull
	@Override
	public String getComponentName() {
		return ScratchComponent.class.getSimpleName();
	}

	@Override
	public void disposeComponent() {
	}

}
