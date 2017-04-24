package com.buuz135.oredowsing.item;

import com.buuz135.oredowsing.util.StringUtil;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

public class ItemEnergy extends ItemBase {

    @Getter
    private int maxPower;
    @Getter
    private int transfer;

    public ItemEnergy(String name, int stackSize, int maxPoxer, int transfer) {
        super(name, stackSize);
        this.maxPower = maxPoxer;
        this.transfer = transfer;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage != null) {
                NumberFormat format = NumberFormat.getInstance();
                tooltip.add(StringUtil.getStringWithColor(format.format(storage.getEnergyStored()), TextFormatting.YELLOW) + StringUtil.getStringWithColor("/", TextFormatting.WHITE) +
                        StringUtil.getStringWithColor(format.format(storage.getMaxEnergyStored()), TextFormatting.YELLOW) + StringUtil.getStringWithColor(" Flux", TextFormatting.GOLD));
            }
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage != null)
                return (storage.getMaxEnergyStored() - storage.getEnergyStored()) / (double)storage.getMaxEnergyStored();
        }
        return 0;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        Color color = new Color(52428);
        return MathHelper.rgb(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new EnergyCapabilityProvider(stack, this);
    }

    private static class EnergyCapabilityProvider implements ICapabilityProvider {

        public final CustomEnergyStorage storage;

        public EnergyCapabilityProvider(final ItemStack stack, ItemEnergy item) {
            this.storage = new CustomEnergyStorage(item.getMaxPower(), item.getTransfer(), item.getTransfer()) {
                @Override
                public int getEnergyStored() {
                    if (stack.hasTagCompound()) {
                        return stack.getTagCompound().getInteger("Energy");
                    } else {
                        return 0;
                    }
                }

                @Override
                public void setEnergyStored(int energy) {
                    if (!stack.hasTagCompound()) {
                        stack.setTagCompound(new NBTTagCompound());
                    }

                    stack.getTagCompound().setInteger("Energy", energy);
                }

                @Override
                public int getMaxEnergyStored() {
                    if (stack.hasTagCompound() && stack.getTagCompound().hasKey("tier")) {
                        return 10000*(stack.getTagCompound().getInteger("tier"));
                    } else {
                        return  10000;
                    }
                }
            };
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return this.getCapability(capability, facing) != null;
        }

        @Nullable
        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == CapabilityEnergy.ENERGY) {
                return (T) this.storage;
            }
            return null;
        }
    }
}
