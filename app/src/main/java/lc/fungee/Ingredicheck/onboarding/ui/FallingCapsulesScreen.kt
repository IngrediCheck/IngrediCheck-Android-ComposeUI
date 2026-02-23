    package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.*
import kotlin.random.Random

/**
 * Simplified Falling Capsules Screen.
 * Uses AABB (box) physics for stacking with a subtle bounce and NO rotation on impact.
 */
@Composable
fun FallingCapsulesScreen(
    modifier: Modifier = Modifier,
    seed: Int = remember { Random.nextInt() },
    spawnIntervalMs: Long = 280L, // Slightly faster spawn
    maxCapsules: Int = 22, // More capsules to fill the bottom
    gravity: Float = 3200f,
    bottomInset: Dp = 0.dp
) {
    val bodies = remember { mutableStateListOf<CapsuleBody>() }
    // Ensure we use the provided seed, but it's generated once per composition
        val rng = remember(seed) { Random(seed) }
        // One capsule per spec, shuffled so the order feels organic and non‑repeating.
        val specs = remember(seed) { defaultCapsuleSpecs().shuffled(rng) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }

        // Effective height is reduced by bottomInset so capsules stop at the
        // top edge of whatever content sits on top (e.g., a bottom sheet).
        val effectiveHeightDp = (maxHeight - bottomInset).coerceAtLeast(0.dp)
        val heightPx = with(density) { effectiveHeightDp.toPx() }

        var containerW by remember { mutableFloatStateOf(0f) }
        var containerH by remember { mutableFloatStateOf(0f) }
        containerW = widthPx
        containerH = heightPx

        var nextId by remember { mutableIntStateOf(0) }
        var nextSpecIndex by remember { mutableIntStateOf(0) }

        // Spawn Loop
        LaunchedEffect(containerW, containerH, seed) {
            if (containerW <= 0f || containerH <= 0f) return@LaunchedEffect
            bodies.clear()
            nextId = 0
            nextSpecIndex = 0

            // Only spawn as many capsules as we have unique specs.
            val maxUnique = min(maxCapsules, specs.size)

            while (isActive && bodies.size < maxUnique && nextSpecIndex < specs.size) {
                // Anti-Stacking Rhythm: Add varied delay per chip
                delay(spawnIntervalMs + rng.nextLong(0, 150))

                val spec = specs[nextSpecIndex++]
                bodies.add(
                    createCapsule(
                        id = nextId++,
                        spec = spec,
                        rng = rng,
                        containerW = containerW,
                        density = density
                    )
                )
            }
        }

        // Physics Simulation Loop
        LaunchedEffect(containerW, containerH, gravity) {
            if (containerW <= 0f || containerH <= 0f) return@LaunchedEffect

            var lastNanos = 0L
            while (isActive) {
                withFrameNanos { now ->
                    if (lastNanos == 0L) {
                        lastNanos = now
                        return@withFrameNanos
                    }
                    val dt = ((now - lastNanos).coerceAtMost(32_000_000L)) / 1_000_000_000f
                    lastNanos = now

                    // Sub-stepping for stability
                    val steps = 4
                    val subDt = dt / steps
                    
                    repeat(steps) {
                        // 1. Apply Gravity & Movement
                        for (i in bodies.indices) {
                            val b = bodies[i]
                            
                            // Simple gravity model similar to typical falling‑text demos:
                            // each capsule accelerates downward with gravity scaled by its mass.
                            val effectiveGravity = if (b.isSettled) 0f else gravity / b.mass
                            
                            // Flight Jitter: Subtle horizontal force to prevent towers
                            val jitter = if (!b.isSettled) (rng.nextFloat() - 0.5f) * 80f else 0f
                            
                            val nextVy = b.vy + effectiveGravity * subDt
                            val nextVx = (b.vx + jitter * subDt) * 0.95f // Air friction
                            
                            val nextX = b.x + nextVx * subDt
                            val nextY = b.y + nextVy * subDt

                            bodies[i] = b.copy(
                                x = nextX,
                                y = nextY,
                                vx = nextVx,
                                vy = nextVy
                            )
                        }

                        // 2. Resolve Boundary & Stacking
                        resolveCollisions(bodies, containerW, containerH, rng)
                        
                        // 3. Post-Collision: Check support stability
                        // Slide-Heavy: 55% support needed to stay settled
                        for (i in bodies.indices) {
                            val b = bodies[i]
                            if (b.isSettled && b.y + b.h < containerH - 1f) {
                                var hasSupport = false
                                for (j in bodies.indices) {
                                    if (i == j) continue
                                    val other = bodies[j]
                                    val overlapX = min(b.x + b.w, other.x + other.w) - max(b.x, other.x)
                                    // Requirement: at least 55% overlap to stay settled
                                    if (abs(other.y - (b.y + b.h)) < 5f && overlapX > b.w * 0.55f) {
                                        hasSupport = true
                                        break
                                    }
                                }
                                if (!hasSupport) {
                                    bodies[i] = b.copy(isSettled = false)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Render
        Box(modifier = Modifier.fillMaxSize()) {
            for (b in bodies) {
                CapsuleChip(
                    body = b,
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = b.x
                            translationY = b.y
                            rotationZ = b.rotationDeg
                        }
                )
            }
        }
    }
}

private data class CapsuleBody(
    val id: Int,
    val label: String,
    val gradient: List<Color>,
    val w: Float,
    val h: Float,
    val mass: Float,
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val rotationDeg: Float,
    val isSettled: Boolean = false,
    val hasBounced: Boolean = false
)

private fun createCapsule(
    id: Int,
    spec: CapsuleSpec,
    rng: Random,
    containerW: Float,
    density: Density
): CapsuleBody {
    // Vary mass slightly per capsule so some fall / settle a bit differently.
    val mass = 0.8f + rng.nextFloat() * 0.7f // 0.8 .. 1.5

    val widthDp = rng.nextInt(spec.minWidthDp.value.toInt(), spec.maxWidthDp.value.toInt() + 1).dp
    val heightDp = spec.heightDp

    val wPx = with(density) { widthDp.toPx() }
    val hPx = with(density) { heightDp.toPx() }

    // Extreme Spread Spawning: 120% coverage (-10% to 110%)
    val startX = (rng.nextFloat() * 1.2f - 0.1f) * containerW
    val y = -hPx * 4f
    
    // Guide Velocity: Guide chips from extreme edges back toward the center area
    val centerX = containerW / 2f
    val centerAttract = (centerX - startX) * 0.4f
    val vx = (rng.nextFloat() - 0.5f) * 450f + centerAttract
    val vy = 100f + rng.nextFloat() * 100f
    val rot = (rng.nextFloat() - 0.5f) * 20f

    return CapsuleBody(
        id = id,
        label = spec.label,
        gradient = spec.gradient,
        w = wPx,
        h = hPx,
        mass = mass,
        x = startX,
        y = y,
        vx = vx,
        vy = vy,
        rotationDeg = rot
    )
}

private fun resolveCollisions(
    bodies: MutableList<CapsuleBody>,
    containerW: Float,
    containerH: Float,
    rng: Random
) {
    val restitution = 0.05f 
    val settleThreshold = 160f

    bodies.indices.forEach { i ->
        val b = bodies[i]

        // Floor
        if (b.y + b.h > containerH) {
            if (!b.hasBounced && abs(b.vy) > settleThreshold) {
                bodies[i] = b.copy(y = containerH - b.h, vy = -b.vy * restitution, hasBounced = true)
            } else {
                bodies[i] = b.copy(y = containerH - b.h, vy = 0f, isSettled = true)
            }
        }

        // Walls
        if (b.x < 0) {
            bodies[i] = bodies[i].copy(x = 0f, vx = abs(b.vx) * 0.15f)
        } else if (b.x + b.w > containerW) {
            bodies[i] = bodies[i].copy(x = containerW - b.w, vx = -abs(b.vx) * 0.15f)
        }
    }

    // Inter-capsule stacking & sliding
    repeat(2) {
        for (i in 0 until bodies.size - 1) {
            for (j in i + 1 until bodies.size) {
                val a = bodies[i]
                val b = bodies[j]

                val overlapX = min(a.x + a.w, b.x + b.w) - max(a.x, b.x)
                val overlapY = min(a.y + a.h, b.y + b.h) - max(a.y, b.y)

                if (overlapX > 0 && overlapY > 0) {
                    // Slide-Heavy Threshold: 55% horizontal overlap
                    val isVertical = (overlapY < overlapX) && (overlapX > min(a.w, b.w) * 0.55f)
                    
                    if (isVertical) {
                        if (a.y < b.y) {
                            if (!a.hasBounced && abs(a.vy) > settleThreshold) {
                                bodies[i] = a.copy(y = a.y - overlapY, vy = -a.vy * restitution, hasBounced = true)
                            } else {
                                bodies[i] = a.copy(y = a.y - overlapY, vy = 0f, isSettled = true)
                            }
                        } else {
                            if (!b.hasBounced && abs(b.vy) > settleThreshold) {
                                bodies[j] = b.copy(y = b.y - overlapY, vy = -b.vy * restitution, hasBounced = true)
                            } else {
                                bodies[j] = b.copy(y = b.y - overlapY, vy = 0f, isSettled = true)
                            }
                        }
                    } else {
                        // Aggressive Slide Force
                        val nudge = if (overlapX < min(a.w, b.w) * 0.3f) 2.2f else 1.2f
                        val push = (overlapX / 2f) * nudge
                        
                        if (a.x < b.x) {
                            bodies[i] = a.copy(x = a.x - push, vx = -abs(a.vx) * 0.4f, isSettled = false)
                            bodies[j] = b.copy(x = b.x + push, vx = abs(b.vx) * 0.4f, isSettled = false)
                        } else {
                            bodies[i] = a.copy(x = a.x + push, vx = abs(a.vx) * 0.4f, isSettled = false)
                            bodies[j] = b.copy(x = b.x - push, vx = -abs(b.vx) * 0.4f, isSettled = false)
                        }
                    }
                }
            }
        }
    }
}

private data class CapsuleSpec(
    val label: String,
    val gradient: List<Color>,
    val minWidthDp: Dp,
    val maxWidthDp: Dp,
    val heightDp: Dp
)

private fun defaultCapsuleSpecs(): List<CapsuleSpec> {
    return listOf(
        // Order and labels (including emoji) match iOS ChipCategory list.
            CapsuleSpec("🫒   Mediterranean", listOf(Color(0xFFF6A54F), Color(0xFFF07A2D)), 150.dp, 190.dp, 38.dp),
        CapsuleSpec("🥛   Dairy Free", listOf(Color(0xFF8D6BFF), Color(0xFF6A4BFF)), 120.dp, 155.dp, 38.dp),
        CapsuleSpec("🍃   Organic Only", listOf(Color(0xFFFF6D77), Color(0xFFFF3D4E)), 130.dp, 170.dp, 38.dp),
        CapsuleSpec("🥩   Paleo", listOf(Color(0xFFFF8A65), Color(0xFFFF7043)), 95.dp, 125.dp, 38.dp),
        CapsuleSpec("🍓   Low Sugar", listOf(Color(0xFFFFB74D), Color(0xFFFF9800)), 115.dp, 150.dp, 38.dp),
        CapsuleSpec("🥦   Vegetarian", listOf(Color(0xFF9CCC65), Color(0xFF7CB342)), 120.dp, 155.dp, 38.dp),
        CapsuleSpec("🫀   Heart Health", listOf(Color(0xFFFF8DA1), Color(0xFFFF5D7E)), 135.dp, 175.dp, 38.dp),
        CapsuleSpec("🐚   Molluscs", listOf(Color(0xFFFF8A80), Color(0xFFFF5252)), 110.dp, 145.dp, 38.dp),
        CapsuleSpec("🍗   High Protein", listOf(Color(0xFF4FA0FF), Color(0xFF297BFF)), 140.dp, 175.dp, 38.dp),
        CapsuleSpec("🥬   Celery", listOf(Color(0xFF66BB6A), Color(0xFF43A047)), 95.dp, 125.dp, 38.dp),
        CapsuleSpec("🥑   Low Fat", listOf(Color(0xFF7FE0FF), Color(0xFF4BC7F8)), 110.dp, 140.dp, 38.dp),
        CapsuleSpec("🌾   Gluten", listOf(Color(0xFFFFA726), Color(0xFFFF8F00)), 95.dp, 125.dp, 38.dp)
    )
}

@Composable
private fun CapsuleChip(
    body: CapsuleBody,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(percent = 50)
    val brush = remember(body.gradient) { Brush.horizontalGradient(body.gradient) }

    Surface(
        modifier = modifier
            .size(with(LocalDensity.current) { body.w.toDp() }, with(LocalDensity.current) { body.h.toDp() }),
        shape = shape,
        color = Color.Transparent,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .clip(shape)
                .background(brush)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = body.label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FallingCapsulesScreenPreview() {
    FallingCapsulesScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6)),
        seed = 42,
        spawnIntervalMs = 300L,
        maxCapsules = 16
    )
}
