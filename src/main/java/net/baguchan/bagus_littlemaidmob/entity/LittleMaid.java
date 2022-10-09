package net.baguchan.bagus_littlemaidmob.entity;

import net.baguchan.bagus_littlemaidmob.entity.goal.MaidRangedCrossbowAttackGoal;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;

//NM読み込むのに要る
public class LittleMaid extends LittleMaidBaseEntity implements RangedAttackMob, CrossbowAttackMob {

    public LittleMaid(EntityType<? extends LittleMaid> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MaidAvoidEntityGoal<>(this, Goat.class, 24.0F, 1.25D, 1.25D));
        this.goalSelector.addGoal(3, new MaidAvoidEntityGoal<>(this, AbstractIllager.class, 24.0F, 1.25D, 1.25D));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.05D, true) {
            @Override
            public boolean canUse() {
                return !isOrderedToSit() && isContract() && (getMainHandItem().getItem() instanceof SwordItem || getMainHandItem().getItem() instanceof AxeItem) && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !isOrderedToSit() && isContract() && (getMainHandItem().getItem() instanceof SwordItem || getMainHandItem().getItem() instanceof AxeItem) && super.canContinueToUse();
            }

            @Override
            protected double getAttackReachSqr(LivingEntity p_25556_) {
                return isTame() ? super.getAttackReachSqr(p_25556_) + 0.5F : super.getAttackReachSqr(p_25556_);
            }
        });
        this.goalSelector.addGoal(4, new RangedAttackGoal(this, 1.0D, 60, 14.0F) {
            @Override
            public boolean canUse() {
                return !isOrderedToSit() && (getMainHandItem().getItem() instanceof BowItem) && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !isOrderedToSit() && (getMainHandItem().getItem() instanceof BowItem) && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(4, new MaidRangedCrossbowAttackGoal(this, 1.0D, 14.0F) {
            @Override
            public boolean canUse() {
                return !isOrderedToSit() && (getMainHandItem().getItem() instanceof CrossbowItem) && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !isOrderedToSit() && (getMainHandItem().getItem() instanceof CrossbowItem) && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.2D, 12.0F, 4.0F, false));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.registerTargetGoals();
    }

    protected void registerTargetGoals() {
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
    }

    public boolean wantsToAttack(LivingEntity p_30389_, LivingEntity p_30390_) {
        if (!(p_30389_ instanceof Creeper) && !(p_30389_ instanceof Ghast)) {
            if (p_30389_ instanceof LittleMaid) {
                LittleMaid littlemaid = (LittleMaid) p_30389_;
                return !littlemaid.isTame() || littlemaid.getOwner() != p_30390_;
            } else if (p_30389_ instanceof Player && p_30390_ instanceof Player && !((Player) p_30390_).canHarmPlayer((Player) p_30389_)) {
                return false;
            } else if (p_30389_ instanceof AbstractHorse && ((AbstractHorse) p_30389_).isTamed()) {
                return false;
            } else {
                return !(p_30389_ instanceof TamableAnimal) || !((TamableAnimal) p_30389_).isTame();
            }
        } else {
            return false;
        }
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
        ItemStack itemstack = this.getProjectile(this.getMainHandItem());
        InteractionHand hand = this.getUsedItemHand();
        if (this.getMainHandItem().getItem() instanceof CrossbowItem) {
            this.performCrossbowAttack(this, 1.6F);
            itemstack.shrink(1);
            this.getMainHandItem().hurtAndBreak(1, this, (playerEntity) -> playerEntity.broadcastBreakEvent(hand));
        } else {
            if (itemstack.getItem() instanceof ArrowItem) {
                AbstractArrow abstractarrow = ProjectileUtil.getMobArrow(this, itemstack, p_33318_);
                if (this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem)
                    abstractarrow = ((net.minecraft.world.item.BowItem) this.getMainHandItem().getItem()).customArrow(abstractarrow);
                double d0 = p_33317_.getX() - this.getX();
                double d1 = p_33317_.getY(0.3333333333333333D) - abstractarrow.getY();
                double d2 = p_33317_.getZ() - this.getZ();
                double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                abstractarrow.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.7F, (float) (2D));
                this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                this.level.addFreshEntity(abstractarrow);
                itemstack.shrink(1);
                this.getMainHandItem().hurtAndBreak(1, this, (playerEntity) -> playerEntity.broadcastBreakEvent(hand));
            }
        }
    }

    @Override
    public void setChargingCrossbow(boolean p_32339_) {

    }

    @Override
    public void shootCrossbowProjectile(LivingEntity p_32328_, ItemStack p_32329_, Projectile p_32330_, float p_32331_) {
        this.shootCrossbowProjectile(this, p_32328_, p_32330_, p_32331_, 1.6F);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    public class MaidAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final LittleMaid littlemaid;

        public MaidAvoidEntityGoal(LittleMaid p_30454_, Class<T> p_30455_, float p_30456_, double p_30457_, double p_30458_) {
            super(p_30454_, p_30455_, p_30456_, p_30457_, p_30458_);
            this.littlemaid = p_30454_;
        }

        public boolean canUse() {
            if (!this.littlemaid.isTame()) {
                return super.canUse();
            } else {
                return false;
            }
        }

        public void start() {
            this.littlemaid.setTarget((LivingEntity) null);
            super.start();
        }

        public void tick() {
            this.littlemaid.setTarget((LivingEntity) null);
            super.tick();
        }
    }
}
