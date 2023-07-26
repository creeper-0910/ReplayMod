package com.replaymod.gui.versions.forge;

import java.util.Collection;

import com.google.common.collect.Collections2;
import com.mojang.blaze3d.vertex.PoseStack;
import com.replaymod.gui.utils.EventRegistrations;
import com.replaymod.gui.versions.callbacks.InitScreenCallback;
import com.replaymod.gui.versions.callbacks.OpenGuiScreenCallback;
import com.replaymod.gui.versions.callbacks.PostRenderScreenCallback;
import com.replaymod.gui.versions.callbacks.PreTickCallback;
import com.replaymod.gui.versions.callbacks.RenderHudCallback;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventsAdapter extends EventRegistrations {
    public static Screen getScreen(GuiScreenEvent event) {
        return event.getGui();
    }

    public static Collection<AbstractWidget> getButtonList(GuiScreenEvent.InitGuiEvent event) {
        return Collections2.transform(Collections2.filter(event.getListenersList(), it -> it instanceof AbstractWidget), it -> (AbstractWidget)it);
    }

    @SubscribeEvent
    public void preGuiInit(GuiScreenEvent.InitGuiEvent.Pre event) {
        InitScreenCallback.Pre.EVENT.invoker().preInitScreen(getScreen(event));
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        InitScreenCallback.EVENT.invoker().initScreen(getScreen(event), getButtonList(event));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiClosed(GuiOpenEvent event) {
        OpenGuiScreenCallback.EVENT.invoker().openGuiScreen(
                event.getGui()
        );
    }

    public static float getPartialTicks(RenderGameOverlayEvent event) {
        return event.getPartialTicks();
    }

    public static float getPartialTicks(GuiScreenEvent.DrawScreenEvent.Post event) {
        return event.getRenderPartialTicks();
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        PostRenderScreenCallback.EVENT.invoker().postRenderScreen(new MatrixStack(), getPartialTicks(event));
    }

    // Even when event was cancelled cause Lunatrius' InGame-Info-XML mod cancels it and we don't actually care about
    // the event (i.e. the overlay text), just about when it's called.
    @SubscribeEvent(receiveCanceled = true)
    public void renderOverlay(RenderGameOverlayEvent.Text event) {
        RenderHudCallback.EVENT.invoker().renderHud(new PoseStack(), getPartialTicks(event));
    }

    @SubscribeEvent
    public void tickOverlay(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            PreTickCallback.EVENT.invoker().preTick();
        }
    }
}
