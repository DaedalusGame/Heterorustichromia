// Made with Blockbench
// Paste this code into your mod.
// Make sure to generate all required imports

public class custom_model extends ModelBase {
	private final ModelRenderer leg_r;
	private final ModelRenderer bone2;
	private final ModelRenderer body;
	private final ModelRenderer arm_r;
	private final ModelRenderer arm_l;
	private final ModelRenderer head;
	private final ModelRenderer bone6;
	private final ModelRenderer bone7;
	private final ModelRenderer bone8;

	public custom_model() {
		textureWidth = 64;
		textureHeight = 64;

		leg_r = new ModelRenderer(this);
		leg_r.setRotationPoint(3.0F, 18.0F, 0.0F);
		leg_r.cubeList.add(new ModelBox(leg_r, 32, 0, -1.0F, 1.0F, -1.5F, 3, 5, 3, 0.0F, false));
		leg_r.cubeList.add(new ModelBox(leg_r, 32, 8, -0.5F, 0.0F, -1.0F, 2, 1, 2, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(-3.0F, 18.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 32, 0, -2.0F, 1.0F, -1.5F, 3, 5, 3, 0.0F, true));
		bone2.cubeList.add(new ModelBox(bone2, 32, 8, -1.5F, 0.0F, -1.0F, 2, 1, 2, 0.0F, true));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 12.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 26, -3.0F, 0.0F, -3.0F, 6, 7, 6, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 0, 39, -3.0F, -0.1F, -3.5F, 6, 9, 7, 0.0F, false));

		arm_r = new ModelRenderer(this);
		arm_r.setRotationPoint(-3.0F, 12.0F, 0.0F);
		setRotationAngle(arm_r, 0.0F, 0.0F, 0.6109F);
		arm_r.cubeList.add(new ModelBox(arm_r, 32, 11, -3.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));
		arm_r.cubeList.add(new ModelBox(arm_r, 32, 21, -2.5F, 6.0F, -1.5F, 3, 2, 3, 0.0F, false));

		arm_l = new ModelRenderer(this);
		arm_l.setRotationPoint(3.0F, 12.0F, 0.0F);
		setRotationAngle(arm_l, 0.0F, 0.0F, -0.6109F);
		arm_l.cubeList.add(new ModelBox(arm_l, 32, 11, -1.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, true));
		arm_l.cubeList.add(new ModelBox(arm_l, 32, 21, -0.5F, 6.0F, -1.5F, 3, 2, 3, 0.0F, true));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 12.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, -6.0F, 4.0F);
		setRotationAngle(bone6, -0.3491F, 0.0F, 0.0F);
		head.addChild(bone6);
		bone6.cubeList.add(new ModelBox(bone6, 0, 16, -2.5F, -4.0F, -5.0F, 5, 5, 5, 0.0F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, -4.0F, -2.0F);
		setRotationAngle(bone7, -0.4363F, 0.0F, 0.0F);
		bone6.addChild(bone7);
		bone7.cubeList.add(new ModelBox(bone7, 20, 16, -0.5F, -6.0F, -1.0F, 1, 7, 2, 0.0F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(-0.5F, -5.0F, 2.0F);
		setRotationAngle(bone8, -0.3491F, 0.0F, 0.0F);
		bone7.addChild(bone8);
		bone8.cubeList.add(new ModelBox(bone8, 26, 16, 0.0F, -3.0F, -3.0F, 1, 3, 2, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		leg_r.render(f5);
		bone2.render(f5);
		body.render(f5);
		arm_r.render(f5);
		arm_l.render(f5);
		head.render(f5);
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}