package net.Leease.leasemod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class SlashParticle extends TextureSheetParticle {

    public SlashParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.lifetime = 5;
        this.scale(0.5F);
        this.rCol = 1.0F;
        this.gCol = 0.0F;
        this.bCol = 0.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            SlashParticle particle = new SlashParticle(level, x, y, z);
            particle.pickSprite(sprites);
            return particle;
        }
    }
}