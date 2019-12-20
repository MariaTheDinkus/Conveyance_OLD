package com.zundrel.conveyance.common.blocks.decor;

import net.minecraft.block.Block;

public class BlockCatwalkPillar extends Block {
//	public static final PropertyBool NE = PropertyBool.create("ne");
//	public static final PropertyBool SE = PropertyBool.create("se");
//	public static final PropertyBool SW = PropertyBool.create("sw");
//	public static final PropertyBool NW = PropertyBool.create("nw");
//
	public BlockCatwalkPillar(Settings settings) {
		super(settings);
	}
//
//	@Override
//	protected BlockStateContainer createBlockState() {
//		return new BlockStateContainer(this, NE, SW, SE, NW);
//	}
//
//	@Override
//	public int getMetaFromState(IBlockState state) {
//		return 0;
//	}
//
//	@Override
//	public IBlockState getStateFromMeta(int meta) {
//		return super.getStateFromMeta(meta);
//	}
//
//	@Override
//	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
//		boolean[] connected = getConnections(worldIn, pos);
//		for (Corner corner : Corner.values()) {
//			state = state.withProperty(corner.prop, connected[corner.ordinal()]);
//		}
//		return state;
//	}
//
//	private boolean[] getConnections(IBlockAccess world, BlockPos pos) {
//		boolean[] res = new boolean[4];
//
//		boolean frameXPos = world.getBlockState(pos.east()).getBlock() == LogisticalAutomation.Blocks.catwalk_pillar;
//		boolean frameXNeg = world.getBlockState(pos.west()).getBlock() == LogisticalAutomation.Blocks.catwalk_pillar;
//		boolean frameZPos = world.getBlockState(pos.south()).getBlock() == LogisticalAutomation.Blocks.catwalk_pillar;
//		boolean frameZNeg = world.getBlockState(pos.north()).getBlock() == LogisticalAutomation.Blocks.catwalk_pillar;
//
//		res[Corner.SE.ordinal()]  = frameXPos || frameZPos;
//		res[Corner.NE.ordinal()]  = frameXPos || frameZNeg;
//		res[Corner.SW.ordinal()]  = frameXNeg || frameZPos;
//		res[Corner.NW.ordinal()]  = frameXNeg || frameZNeg;
//
//		return res;
//	}
//
//	@Override
//	@Nonnull
//	@SideOnly(Side.CLIENT)
//	public BlockRenderLayer getBlockLayer() {
//		return BlockRenderLayer.CUTOUT;
//	}
//
//	@Override
//	public boolean isFullCube(IBlockState state) {
//		return false;
//	}
//
//	@Override
//	public boolean isOpaqueCube(IBlockState blockState) {
//		return false;
//	}
//
//	@Nullable
//	@Override
//	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
//		return NULL_AABB;
//	}
//
//	/**
//	 * @author TeamPnuematic
//	 * Thank you to TeamPneumatic for their code from the elevator frame.
//	 */
//	private enum Corner {
//		NE(1, -1, BlockCatwalkPillar.NE, new AxisAlignedBB(14f / 16f, 0, 0, 1, 1, 2f/16f)),
//		SE(1, 1, BlockCatwalkPillar.SE, new AxisAlignedBB(14f / 16f, 0, 14f / 16f, 1, 1, 1)),
//		SW(-1, 1, BlockCatwalkPillar.SW, new AxisAlignedBB(0, 0, 14f / 16f, 2f / 16f, 1, 1)),
//		NW(-1,-1, BlockCatwalkPillar.NW, new AxisAlignedBB(0, 0, 0, 2f/16f, 1, 2f/16f));
//
//		final int x;
//		final int z;
//		final PropertyBool prop;
//		final AxisAlignedBB aabb;
//
//		Corner(int x, int z, PropertyBool prop, AxisAlignedBB aabb) {
//			this.x = x; this.z = z;
//			this.prop = prop;
//			this.aabb = aabb;
//		}
//	}
}
