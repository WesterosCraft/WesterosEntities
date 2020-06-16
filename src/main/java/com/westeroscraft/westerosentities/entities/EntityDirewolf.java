package com.westeroscraft.westerosentities.entities;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public class EntityDirewolf extends EntityWolf {

    // Direwolf growth stages
    // How much time it takes to get to a certain stage, in milliseconds
    public static final long PUP = 0L; // 0 days
    public static final long YOUNG = 604800000L; // 7 days
    public static final long ADULT = 1209600000L; // 14 days
    public static final long SENIOR = 2419200000L; // 28 days

    // define direwolf colors
    public static final int AMBER = 0;
    public static final int BLACK = 1;
    public static final int BLACKWHITE = 2;
    public static final int BROWN = 3;
    public static final int CHESTNUT = 4;
    public static final int CINNAMON = 5;
    public static final int GREY = 6;
    public static final int GREYWHITE = 7;
    public static final int MIXED = 8;
    public static final int TAN = 9;
    public static final int WHITE = 10;
    public static final int WHITEGREY = 11;
    public static final int AMBER_ANGRY = 0 + 12;
    public static final int BLACK_ANGRY = 1 + 12;
    public static final int BLACKWHITE_ANGRY = 2 + 12;
    public static final int BROWN_ANGRY = 3 + 12;
    public static final int CHESTNUT_ANGRY = 4 + 12;
    public static final int CINNAMON_ANGRY = 5 + 12;
    public static final int GREY_ANGRY = 6 + 12;
    public static final int GREYWHITE_ANGRY = 7 + 12;
    public static final int MIXED_ANGRY = 8 + 12;
    public static final int TAN_ANGRY = 9 + 12;
    public static final int WHITE_ANGRY = 10 + 12;
    public static final int WHITEGREY_ANGRY = 11 + 12;

    // a logger for logging purposes
    private static final Logger LOGGER = LogManager.getLogger();

    // this specific direwolf's color
    private static final DataParameter<Integer> DATA_VARIANT_ID = EntityDataManager.createKey(EntityDirewolf.class, DataSerializers.VARINT);

    // this specific direwolf's growth stage
    private static final DataParameter<Integer> DATA_GROWTH_STAGE = EntityDataManager.createKey(EntityDirewolf.class, DataSerializers.VARINT);

    public EntityDirewolf(World worldIn) {
        super(worldIn);
    }

    /*
    This funky override fiddles with the follow owner AI task. Direwolves are pretty big so they don't stand as close.
    Direwolves also have no quarrel with llamas, unlike their smaller brethren.
    Most of the code here is a direct copy & paste from EntityWolf: only the EntityAIFollowOwner task has been modified.
    */
    @Override
    protected void initEntityAI() {
        this.aiSit = new EntityAISit(this);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, true));

        // this is the only task that is changed from EntityWolf
        // this is a pretty bad way to implement this but I couldn't find a better one
        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 5.0F));

        this.tasks.addTask(6, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(8, new EntityAIBeg(this, 8.0F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityAnimal.class, false, new Predicate<Entity>() {
            public boolean apply(@Nullable Entity p_apply_1_) {
                return p_apply_1_ instanceof EntitySheep || p_apply_1_ instanceof EntityRabbit;
            }
        }));
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, AbstractSkeleton.class, false));
    }
  
    // every time a direwolf is updated, its scale is changed based on its growth stage
    // this is so hitboxes remain accurate to the model size
    @Override
    public void onUpdate() {
        super.onUpdate();
        setScale((getGrowthStage() / 3f) + (2f / 3f));
    }

    // this method is used while minecraft is rendering shadow sizes
    // we pass a value that scales with a wolf's growth stage
    @Override
    public float getRenderSizeModifier() {
        return (getGrowthStage() / 3f) + (2f / 3f);
      
    // direwolves are invulnerable to everything
    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return true;
    }

    // saves the entity over restarts. we specifically need to preserve its variant variable
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Variant", this.getVariant());
    }

    // see above. this method does the reverse
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setVariant(compound.getInteger("Variant"));
    }

    // a special method where we need to register our DataParameters
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(DATA_VARIANT_ID, ThreadLocalRandom.current().nextInt(12));
        this.dataManager.register(DATA_GROWTH_STAGE, ThreadLocalRandom.current().nextInt(1, 5));
    }

    // a random variant and growth stage is selected on initial spawn
    // this is overwritten later if the player specifies a variant and growth stage of choice in the /wcdirewolf command
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setVariant(ThreadLocalRandom.current().nextInt(12));
        this.setGrowthStage(ThreadLocalRandom.current().nextInt(1, 5));
        return livingdata;
    }

    // direwolves cannot produce children
    @Override
    public EntityWolf createChild(EntityAgeable e) {
        return null;
    }

    // getter
    public int getVariant() {
        return dataManager.get(DATA_VARIANT_ID);
    }

    // setter
    public void setVariant(int variant) {
        dataManager.set(DATA_VARIANT_ID, variant);
    }

    // getter for the growth stage
    public int getGrowthStage() {
        return dataManager.get(DATA_GROWTH_STAGE);
    }

    // setter for the growth stage
    public void setGrowthStage (int growthStage) {
        dataManager.set(DATA_GROWTH_STAGE, growthStage);
    }
}
