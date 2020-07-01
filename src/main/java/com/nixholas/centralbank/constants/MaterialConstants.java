package com.nixholas.centralbank.constants;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class MaterialConstants {
    public final static Collection<Material> Signs = new ArrayList<Material>(
            Arrays.asList(Material.ACACIA_SIGN, Material.BIRCH_SIGN, Material.DARK_OAK_SIGN, Material.SPRUCE_SIGN,
                    Material.JUNGLE_SIGN, Material.OAK_SIGN, Material.ACACIA_WALL_SIGN, Material.BIRCH_WALL_SIGN,
                    Material.DARK_OAK_WALL_SIGN, Material.SPRUCE_WALL_SIGN, Material.JUNGLE_WALL_SIGN,
                    Material.OAK_WALL_SIGN, Material.LEGACY_SIGN, Material.LEGACY_WALL_SIGN,
                    Material.LEGACY_SIGN_POST));
    public final static Collection<Material> WalledSigns = new ArrayList<Material>(
            Arrays.asList(Material.ACACIA_WALL_SIGN, Material.BIRCH_WALL_SIGN, Material.DARK_OAK_WALL_SIGN,
                    Material.SPRUCE_WALL_SIGN, Material.JUNGLE_WALL_SIGN, Material.OAK_WALL_SIGN));
    public final static Collection<Material> NonWalledSigns = new ArrayList<Material>(
            Arrays.asList(Material.ACACIA_SIGN, Material.BIRCH_SIGN, Material.DARK_OAK_SIGN, Material.SPRUCE_SIGN,
                    Material.JUNGLE_SIGN, Material.OAK_SIGN));
}
