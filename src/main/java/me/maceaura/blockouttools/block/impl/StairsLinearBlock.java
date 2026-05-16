package me.maceaura.blockouttools.block.impl;

import me.maceaura.blockouttools.block.BlockoutToolsBlock;
import me.maceaura.blockouttools.data.BlockoutProperties;
import me.maceaura.blockouttools.data.property.SlopeType;
import me.maceaura.blockouttools.util.BlockoutPlacementStateBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StairsLinearBlock extends BlockoutToolsBlock {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final EnumProperty<SlopeType> TYPE = BlockoutProperties.SLOPE_TYPE;

    private static final int STEPS = 10;
    private static final double STEP_DEPTH = 16.0 / STEPS;

    protected static final VoxelShape NORTH_BOTTOM_SHAPE, SOUTH_BOTTOM_SHAPE, EAST_BOTTOM_SHAPE, WEST_BOTTOM_SHAPE;
    protected static final VoxelShape NORTH_TOP_SHAPE, SOUTH_TOP_SHAPE, EAST_TOP_SHAPE, WEST_TOP_SHAPE;
    protected static final VoxelShape NORTH_HORIZONTAL_SHAPE, SOUTH_HORIZONTAL_SHAPE, EAST_HORIZONTAL_SHAPE, WEST_HORIZONTAL_SHAPE;

    static {
        VoxelShape northBottom = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            northBottom = VoxelShapes.union(northBottom, Block.createCuboidShape(0.0, STEP_DEPTH * i, STEP_DEPTH * (STEPS - 1 - i), 16.0, STEP_DEPTH * (i + 1), STEP_DEPTH * (STEPS - i)));
        }
        NORTH_BOTTOM_SHAPE = northBottom;

        VoxelShape southBottom = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            southBottom = VoxelShapes.union(southBottom, Block.createCuboidShape(0.0, STEP_DEPTH * i, STEP_DEPTH * i, 16.0, STEP_DEPTH * (i + 1), STEP_DEPTH * (i + 1)));
        }
        SOUTH_BOTTOM_SHAPE = southBottom;

        VoxelShape eastBottom = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            eastBottom = VoxelShapes.union(eastBottom, Block.createCuboidShape(STEP_DEPTH * i, STEP_DEPTH * i, 0.0, STEP_DEPTH * (i + 1), STEP_DEPTH * (i + 1), 16.0));
        }
        EAST_BOTTOM_SHAPE = eastBottom;

        VoxelShape westBottom = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            westBottom = VoxelShapes.union(westBottom, Block.createCuboidShape(STEP_DEPTH * (STEPS - 1 - i), STEP_DEPTH * i, 0.0, STEP_DEPTH * (STEPS - i), STEP_DEPTH * (i + 1), 16.0));
        }
        WEST_BOTTOM_SHAPE = westBottom;

        VoxelShape northTop = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            northTop = VoxelShapes.union(northTop, Block.createCuboidShape(0.0, 16.0 - STEP_DEPTH * (i + 1), STEP_DEPTH * (STEPS - 1 - i), 16.0, 16.0 - STEP_DEPTH * i, STEP_DEPTH * (STEPS - i)));
        }
        NORTH_TOP_SHAPE = northTop;

        VoxelShape southTop = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            southTop = VoxelShapes.union(southTop, Block.createCuboidShape(0.0, 16.0 - STEP_DEPTH * (i + 1), STEP_DEPTH * i, 16.0, 16.0 - STEP_DEPTH * i, STEP_DEPTH * (i + 1)));
        }
        SOUTH_TOP_SHAPE = southTop;

        VoxelShape eastTop = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            eastTop = VoxelShapes.union(eastTop, Block.createCuboidShape(STEP_DEPTH * i, 16.0 - STEP_DEPTH * (i + 1), 0.0, STEP_DEPTH * (i + 1), 16.0 - STEP_DEPTH * i, 16.0));
        }
        EAST_TOP_SHAPE = eastTop;

        VoxelShape westTop = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            westTop = VoxelShapes.union(westTop, Block.createCuboidShape(STEP_DEPTH * (STEPS - 1 - i), 16.0 - STEP_DEPTH * (i + 1), 0.0, STEP_DEPTH * (STEPS - i), 16.0 - STEP_DEPTH * i, 16.0));
        }
        WEST_TOP_SHAPE = westTop;

        VoxelShape northHorizontal = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            northHorizontal = VoxelShapes.union(northHorizontal, Block.createCuboidShape(STEP_DEPTH * (STEPS - 1 - i), 0.0, STEP_DEPTH * i, STEP_DEPTH * (STEPS - i), 16.0, STEP_DEPTH * (i + 1)));
        }
        NORTH_HORIZONTAL_SHAPE = northHorizontal;

        VoxelShape eastHorizontal = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            eastHorizontal = VoxelShapes.union(eastHorizontal, Block.createCuboidShape(STEP_DEPTH * (STEPS - 1 - i), 0.0, STEP_DEPTH * (STEPS - 1 - i), STEP_DEPTH * (STEPS - i), 16.0, STEP_DEPTH * (STEPS - i)));
        }
        EAST_HORIZONTAL_SHAPE = eastHorizontal;

        VoxelShape southHorizontal = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            southHorizontal = VoxelShapes.union(southHorizontal, Block.createCuboidShape(STEP_DEPTH * i, 0.0, STEP_DEPTH * (STEPS - 1 - i), STEP_DEPTH * (i + 1), 16.0, STEP_DEPTH * (STEPS - i)));
        }
        SOUTH_HORIZONTAL_SHAPE = southHorizontal;

        VoxelShape westHorizontal = VoxelShapes.empty();
        for (int i = 0; i < STEPS; i++) {
            westHorizontal = VoxelShapes.union(westHorizontal, Block.createCuboidShape(STEP_DEPTH * i, 0.0, STEP_DEPTH * i, STEP_DEPTH * (i + 1), 16.0, STEP_DEPTH * (i + 1)));
        }
        WEST_HORIZONTAL_SHAPE = westHorizontal;
    }

    public StairsLinearBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, SlopeType.BOTTOM)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return BlockoutPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacingAndSlopeType()
                .withWater()
                .build();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        SlopeType type = state.get(TYPE);

        if (type == SlopeType.TOP) {
            switch (direction) {
                case SOUTH:
                    return SOUTH_TOP_SHAPE;
                case EAST:
                    return EAST_TOP_SHAPE;
                case WEST:
                    return WEST_TOP_SHAPE;
                default:
                    return NORTH_TOP_SHAPE;
            }
        } else if (type == SlopeType.HORIZONTAL) {
            switch (direction) {
                case SOUTH:
                    return SOUTH_HORIZONTAL_SHAPE;
                case EAST:
                    return EAST_HORIZONTAL_SHAPE;
                case WEST:
                    return WEST_HORIZONTAL_SHAPE;
                default:
                    return NORTH_HORIZONTAL_SHAPE;
            }
        } else {
            switch (direction) {
                case SOUTH:
                    return SOUTH_BOTTOM_SHAPE;
                case EAST:
                    return EAST_BOTTOM_SHAPE;
                case WEST:
                    return WEST_BOTTOM_SHAPE;
                default:
                    return NORTH_BOTTOM_SHAPE;
            }
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }
}
