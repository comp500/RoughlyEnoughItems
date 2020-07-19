package me.shedaniel.rei.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello yes I am comp500 optimiser here
 */
@Environment(EnvType.CLIENT)
public class EntryListWidgetVertexBufferManager {
	private final List<RenderLayer> layers = new ArrayList<>();
	private final List<VertexBuffer> buffers = new ArrayList<>();
	private final Map<RenderLayer, BufferBuilder> buildingBuffers = new HashMap<>();
	private boolean isValid = false;

	// TODO: Call this on InvalidateRenderStateCallback
	public void reset() {
		layers.clear();
		for (VertexBuffer buf : buffers) {
			buf.close();
		}
		buffers.clear();
		buildingBuffers.clear();
	}

	public void invalidate() {
		isValid = false;
	}

	public boolean isValid() {
		return isValid;
	}

	public VertexConsumerProvider getRebuildingConsumer() {
		return layer -> buildingBuffers.computeIfAbsent(layer, l -> {
			BufferBuilder buf = new BufferBuilder(l.getExpectedBufferSize());
			buf.begin(l.getDrawMode(), l.getVertexFormat());
			return buf;
		});
	}

	public void finishRebuild() {
		// TODO: multithread?
		// Loop through the existing layers, rebuild or remove as necessary (reusing vertex buffers)
		for (int i = 0; i < layers.size(); i++) {
			RenderLayer rl = layers.get(i);
			VertexBuffer buf = buffers.get(i);
			if (buildingBuffers.containsKey(rl)) {
				BufferBuilder bufBuilder = buildingBuffers.get(rl);
				// TODO: reuse BufferBuilders?
				buildingBuffers.remove(rl);
				bufBuilder.sortQuads(0, 0, 0);
				bufBuilder.end();
				buf.upload(bufBuilder);
			} else {
				layers.remove(i);
				buf.close();
				buffers.remove(i);
				i--;
			}
		}
		// Add new VertexBuffers for layers that haven't been seen before
		for (Map.Entry<RenderLayer, BufferBuilder> entry : buildingBuffers.entrySet()) {
			RenderLayer rl = entry.getKey();
			BufferBuilder bufBuilder = entry.getValue();
			VertexBuffer newBuf = new VertexBuffer(rl.getVertexFormat());
			bufBuilder.sortQuads(0, 0, 0);
			bufBuilder.end();
			newBuf.upload(bufBuilder);
			layers.add(rl);
			buffers.add(newBuf);
		}
		buildingBuffers.clear();
		isValid = true;
	}

	public void render(MatrixStack matrices) {
		for (int i = 0; i < layers.size(); i++) {
			RenderLayer rl = layers.get(i);
			VertexBuffer buf = buffers.get(i);
			rl.startDrawing();
			buf.bind();
			rl.getVertexFormat().startDrawing(0L);
			buf.draw(matrices.peek().getModel(), rl.getDrawMode());
			VertexBuffer.unbind();
			rl.getVertexFormat().endDrawing();
			rl.endDrawing();
		}
	}

}
