package zov.viola.module.list.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;

public interface HeldItemRendererAccessor {
   void invokeRenderPlayerArm(MatrixStack var1, VertexConsumerProvider var2, int var3, float var4, float var5, Arm var6);

   void invokeApplyItemArmTransform(MatrixStack var1, Arm var2, float var3);

   void invokeSwingArm(float var1, float var2, MatrixStack var3, int var4, Arm var5);

   void invokeRenderItem(LivingEntity var1, ItemStack var2, ModelTransformationMode var3, boolean var4, MatrixStack var5, VertexConsumerProvider var6, int var7);

   void invokeRenderTwoHandedMap(MatrixStack var1, VertexConsumerProvider var2, int var3, float var4, float var5, float var6);

   void invokeRenderOneHandedMap(MatrixStack var1, VertexConsumerProvider var2, int var3, float var4, Arm var5, float var6, ItemStack var7);

   ItemStack getOffHand();
}
