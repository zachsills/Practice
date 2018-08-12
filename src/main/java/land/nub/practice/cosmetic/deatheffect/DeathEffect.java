package land.nub.practice.cosmetic.deatheffect;

import land.nub.practice.cosmetic.Cosmetic;
import land.nub.practice.util.particle.ParticleEffect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum DeathEffect implements Cosmetic {

    FIRE(DeathEffectType.PARTICLE, "Fire", new ParticleEffect.ParticleData(ParticleEffect.ParticleType.LAVA, 5, 3, 1.2), Material.BLAZE_POWDER, 1000),
    SNOW(DeathEffectType.PARTICLE, "Snow", new ParticleEffect.ParticleData(ParticleEffect.ParticleType.FIREWORKS_SPARK, 0.1, 7, 1.1), Material.SNOW_BALL, 500),
    RAIN(DeathEffectType.PARTICLE, "Rain", new ParticleEffect.ParticleData(ParticleEffect.ParticleType.DRIP_WATER, 3, 5, 0.7), Material.POTION, 700),
    MYSTIC(DeathEffectType.PARTICLE, "Mystic", new ParticleEffect.ParticleData(ParticleEffect.ParticleType.ENCHANTMENT_TABLE, 0.2, 8, 0.7), Material.ENCHANTMENT_TABLE, 1000),
    ENDER(DeathEffectType.PARTICLE, "Ender", new ParticleEffect.ParticleData(ParticleEffect.ParticleType.PORTAL, 0.1, 10, 0.7), Material.ENDER_PEARL, 1000),
    HYPNOTIC(DeathEffectType.PARTICLE, "Hypnotic", new ParticleEffect.ParticleData(ParticleEffect.ParticleType.SPELL_MOB, 30, 4, 1), Material.GHAST_TEAR, 700),
    LOVE(DeathEffectType.PARTICLE, "Love", new ParticleEffect.ParticleData(ParticleEffect.ParticleType.HEART, 10, 2, 0.7), Material.GOLDEN_CARROT, 400),
    NOTES(DeathEffectType.PARTICLE, "Notes", new ParticleEffect.ParticleData(ParticleEffect.ParticleType.NOTE, 10, 3, 1), Material.NOTE_BLOCK, 500),
    EXPLOSION(DeathEffectType.PARTICLE, "Explosion", new ParticleEffect.ParticleData(ParticleEffect.ParticleType.EXPLOSION_NORMAL, 0.3, 3, 0.4), Material.COAL, 700),
    LIGHTNING(DeathEffectType.WORLD, "Lightning", Material.FLINT_AND_STEEL);

    private DeathEffectType type;
    private String name;
    private ParticleEffect.ParticleData data;
    private Material material;
    private double cost;

    DeathEffect(DeathEffectType type, String name, Material material) {
        this.type = type;
        this.name = name;
        this.material = material;
    }
}