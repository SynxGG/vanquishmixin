package vanquishmixin.client.gui;

import vanquishmixin.world.inventory.WorkstationMenu;

import vanquishmixin.init.VanquishmixinModScreens;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import com.mojang.blaze3d.systems.RenderSystem;

public class WorkstationScreen extends AbstractContainerScreen<WorkstationMenu> implements VanquishmixinModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private static final ResourceLocation BACKGROUND = new ResourceLocation("vanquishmixin:textures/screens/workstation.png");
	private static final ResourceLocation IMAGE_0 = new ResourceLocation("vanquishmixin:textures/screens/gui_arrow_right.png");
	private static final ResourceLocation IMAGE_1 = new ResourceLocation("vanquishmixin:textures/screens/crafting_table_output.png");

	public WorkstationScreen(WorkstationMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	@Override
	public void updateMenuState(int elementType, String name, Object elementState) {
		menuStateUpdateActive = true;
		menuStateUpdateActive = false;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiGraphics.blit(IMAGE_0, this.leftPos + 85, this.topPos + 37, 0, 0, 22, 15, 22, 15);
		guiGraphics.blit(IMAGE_1, this.leftPos + 111, this.topPos + 31, 0, 0, 26, 26, 26, 26);
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.vanquishmixin.workstation.label_crafting"), 25, 4, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
	}
}