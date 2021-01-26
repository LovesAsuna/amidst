package amidst.gui.main.menu;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;

import amidst.AmidstSettings;
import amidst.FeatureToggles;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.Actions;
import amidst.gui.main.AmidstLookAndFeel;
import amidst.mojangapi.world.WorldType;
import amidst.settings.Setting;
import amidst.settings.biomeprofile.BiomeProfileDirectory;

@NotThreadSafe
public class AmidstMenuBuilder {
	private final AmidstSettings settings;
	private final Actions actions;
	private final BiomeProfileDirectory biomeProfileDirectory;
	private final JMenuBar menuBar;
	private JMenu worldMenu;
	private JMenuItem savePlayerLocationsMenu;
	private JMenuItem reloadPlayerLocationsMenu;
	private LayersMenu layersMenu;

	public AmidstMenuBuilder(AmidstSettings settings, Actions actions, BiomeProfileDirectory biomeProfileDirectory) {
		this.settings = settings;
		this.actions = actions;
		this.biomeProfileDirectory = biomeProfileDirectory;
		this.menuBar = createMenuBar();
	}

	public AmidstMenu construct() {
		return new AmidstMenu(
				menuBar,
				worldMenu,
				savePlayerLocationsMenu,
				reloadPlayerLocationsMenu,
				layersMenu);
	}

	private JMenuBar createMenuBar() {
		JMenuBar result = new JMenuBar();
		result.add(create_File());
		worldMenu = result.add(create_World());
		result.add(create_Layers());
		result.add(create_Settings());
		result.add(create_Help());
		return result;
	}

	private JMenu create_File() {
		JMenu result = new JMenu("文件");
		result.setMnemonic(KeyEvent.VK_F);
		// @formatter:off
		Menus.item(result, actions::newFromSeed,           "从种子打开 ...",          KeyEvent.VK_N, MenuShortcuts.NEW_FROM_SEED);
		Menus.item(result, actions::newFromRandom,         "从随机种子打开",       KeyEvent.VK_R, MenuShortcuts.NEW_FROM_RANDOM_SEED);
		if (FeatureToggles.SEED_SEARCH) {
			Menus.item(result, actions::searchForRandom,   "Search for Random Seed",     KeyEvent.VK_F, MenuShortcuts.SEARCH_FOR_RANDOM_SEED);
		}
		Menus.item(result, actions::openSaveGame,          "打开已保存的游戏 ...",         KeyEvent.VK_O, MenuShortcuts.OPEN_SAVE_GAME);
		result.addSeparator();
		Menus.item(result, actions::switchProfile,         "切换游戏版本 ...",         KeyEvent.VK_P, MenuShortcuts.SWITCH_PROFILE);
		Menus.item(result, actions::exit,                  "退出",                       KeyEvent.VK_X, MenuShortcuts.EXIT);
		// @formatter:on
		return result;
	}

	private JMenu create_World() {
		JMenu result = new JMenu("世界");
		result.setEnabled(false);
		result.setMnemonic(KeyEvent.VK_W);
		// @formatter:off
		Menus.item(result, actions::goToCoordinate,        "跳转到指定坐标 ...",       KeyEvent.VK_C, MenuShortcuts.GO_TO_COORDINATE);
		Menus.item(result, actions::goToSpawn,             "跳转到出生点",          KeyEvent.VK_S, MenuShortcuts.GO_TO_WORLD_SPAWN);
		Menus.item(result, actions::goToStronghold,        "跳转到要塞 ...",       KeyEvent.VK_H, MenuShortcuts.GO_TO_STRONGHOLD);
		Menus.item(result, actions::goToPlayer,            "跳转到玩家 ...",           KeyEvent.VK_P, MenuShortcuts.GO_TO_PLAYER);
		result.addSeparator();
		Menus.item(result, actions::zoomIn,                "放大",                    KeyEvent.VK_I, MenuShortcuts.ZOOM_IN);
		Menus.item(result, actions::zoomOut,               "缩小",                   KeyEvent.VK_O, MenuShortcuts.ZOOM_OUT);
		result.addSeparator();
		savePlayerLocationsMenu =
				Menus.item(result, actions::savePlayerLocations,   "保存玩家位置",      KeyEvent.VK_V, MenuShortcuts.SAVE_PLAYER_LOCATIONS);
		savePlayerLocationsMenu.setEnabled(false);
		reloadPlayerLocationsMenu =
				Menus.item(result, actions::reloadPlayerLocations, "重载玩家位置",    KeyEvent.VK_R, MenuShortcuts.RELOAD_PLAYER_LOCATIONS);
		reloadPlayerLocationsMenu.setEnabled(false);
		Menus.item(result, actions::howCanIMoveAPlayer,    "怎样移动玩家?",   KeyEvent.VK_M);
		result.addSeparator();
		Menus.item(result, actions::copySeedToClipboard,   "保存地图种子到剪贴板",     KeyEvent.VK_B, MenuShortcuts.COPY_SEED_TO_CLIPBOARD);
		Menus.item(result, actions::takeScreenshot,        "截图 ...",        KeyEvent.VK_T, MenuShortcuts.TAKE_SCREENSHOT);
		result.addSeparator();
		Menus.item(result, actions::openExportDialog,      "导出群系到图片 ....", KeyEvent.VK_X, MenuShortcuts.EXPORT_BIOMES);
		// @formatter:on
		return result;
	}

	private JMenuItem create_Layers() {
		JMenu result = new JMenu("图层");
		result.setMnemonic(KeyEvent.VK_L);
		layersMenu = new LayersMenu(result, settings);
		return result;
	}

	private JMenu create_Settings() {
		JMenu result = new JMenu("设置");
		result.setMnemonic(KeyEvent.VK_S);
		result.add(create_Settings_DefaultWorldType());
		if (biomeProfileDirectory.isValid()) {
			result.add(create_Settings_BiomeProfile());
		}
		result.addSeparator();
		// @formatter:off
		Menus.checkbox(result, settings.smoothScrolling,      "平滑滚动");
		Menus.checkbox(result, settings.fragmentFading,       "Fragment Fading");
		Menus.checkbox(result, settings.maxZoom,              "限制放大倍数");
		Menus.checkbox(result, settings.showFPS,              "显示帧率和CPU");
		Menus.checkbox(result, settings.showScale,            "显示比例尺");
		Menus.checkbox(result, settings.showDebug,            "显示 Debug 信息");
		Menus.checkbox(result, settings.useHybridScaling,     "使用混合缩放");
		// @formatter:on
		result.addSeparator();
		result.add(create_Settings_LookAndFeel());
		result.add(create_Settings_Threads());
		return result;
	}

	private JMenu create_Settings_DefaultWorldType() {
		JMenu result = new JMenu("默认世界类型");
		// @formatter:off
		Menus.radios(result, settings.worldType, WorldType.getWorldTypeSettingAvailableValues());
		// @formatter:on
		return result;
	}

	private JMenu create_Settings_LookAndFeel() {
		JMenu result = new JMenu("Look & Feel");

		List<AbstractButton> radios = new ArrayList<>();
		Setting<AmidstLookAndFeel> lookAndFeelSetting = settings.lookAndFeel.withListener(
			(oldValue, newValue) -> {
				if (!oldValue.equals(newValue) && !actions.tryChangeLookAndFeel(newValue)) {
					settings.lookAndFeel.set(oldValue);
					radios.get(oldValue.ordinal()).setSelected(true);
				}
			});

		radios.addAll(Menus.radios(result, lookAndFeelSetting, AmidstLookAndFeel.values()));
		return result;
	}
	
	private JMenu create_Settings_Threads() {
		UIManager.put("Slider.focus", UIManager.get("Slider.background"));
		int cores = Runtime.getRuntime().availableProcessors();
		JMenu submenu = new JMenu("No. of Threads");
		JSlider slider = new JSlider(JSlider.VERTICAL, 1, cores, settings.threads.get());
		submenu.add(slider);
		slider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getSource() instanceof JSlider) {
					if(settings.threads.get().intValue() != ((JSlider) e.getSource()).getValue()) {
						settings.threads.set(((JSlider) e.getSource()).getValue());
						MenuSelectionManager.defaultManager().clearSelectedPath();
						actions.tryChangeThreads();
					}
				}
			}			
		});
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);
		slider.setPaintLabels(true);
		Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>(3);
		table.put(1, new JLabel("1"));
		table.put(cores / 2, new JLabel("" + cores / 2));
		table.put(cores, new JLabel("" + cores));
		slider.setLabelTable(table);
		return submenu;
	}

	private JMenu create_Settings_BiomeProfile() {
		JMenu result = new JMenu("Biome Profile");
		// @formatter:off
		new BiomeProfileMenuFactory(result, actions, biomeProfileDirectory, "Reload Biome Profiles", KeyEvent.VK_R, MenuShortcuts.RELOAD_BIOME_PROFILES,
																			"Create Example Profile", KeyEvent.VK_C);
		// @formatter:on
		return result;
	}

	private JMenu create_Help() {
		JMenu result = new JMenu("Help");
		result.setMnemonic(KeyEvent.VK_H);
		// @formatter:off
		Menus.item(result, actions::displayLogMessages,    "显示 Log 信息 ...",   KeyEvent.VK_M);
		Menus.item(result, actions::checkForUpdates,       "检查更新 ...",      KeyEvent.VK_U);
		Menus.item(result, actions::viewLicense,           "查看许可协议 ...",          KeyEvent.VK_L);
		Menus.item(result, actions::about,                 "关于 ...",                  KeyEvent.VK_A);
		// @formatter:on
		return result;
	}
}
