package hyperplasia.content.utils;

import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import arc.math.Mathf;
import arc.graphics.g2d.Draw;

public class DeaccelBullet extends BasicBulletType {

    // slowdown strength
    public float deaccelAmount = 0.3f;

    // how large the pulse is (e.g., 0.2 = ±20%)
    public float pulseAmplitude = 0.20f;

    // how fast the pulse oscillates
    public float pulseSpeed = 0.2f;

    public DeaccelBullet(float speed, float damage){
        super(speed, damage);
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        // linear slowdown curve
        float t = b.time / b.lifetime;
        float slow = 1f - t * deaccelAmount;
        b.vel.scl(Mathf.clamp(slow, 0.1f, 1f));
    }

    @Override
    public void draw(Bullet b){
        // smooth sinusoidal pulse
        float raw = Mathf.sin(b.time * pulseSpeed);

        // softened by slope curve to feel organic (optional)
        float eased = raw * Mathf.slope(b.time / b.lifetime);

        // final scale multiplier
        float pulse = 1f + eased * pulseAmplitude;

        // store original
        float origWidth = width;
        float origHeight = height;

        // apply pulse
        width = origWidth * pulse;
        height = origHeight * pulse;

        super.draw(b);

        // restore original
        width = origWidth;
        height = origHeight;
    }
}