package lc.fungee.Ingredicheck.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val MyIcon: ImageVector
    get() {
        if (_MyIcon != null) return _MyIcon!!
        _MyIcon = ImageVector.Builder(
            name = "MyIcon",
            defaultWidth = 193.0.dp,
            defaultHeight = 214.0.dp,
            viewportWidth = 193.0f,
            viewportHeight = 214.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
            ) {
                moveTo(32.0f, 205.0f)
                curveTo(19.2975f, 205.0f, 9.0f, 194.703f, 9.0f, 182.0f)
                lineTo(8.99998f, 32.0f)
                curveTo(8.99998f, 19.2975f, 19.2974f, 9.00001f, 32.0f, 9.00001f)
                lineTo(161.0f, 9.0f)
                curveTo(173.703f, 9.0f, 184.0f, 19.2975f, 184.0f, 32.0f)
                lineTo(184.0f, 126.294f)
                curveTo(184.0f, 140.491f, 172.491f, 152.0f, 158.294f, 152.0f)
                curveTo(144.097f, 152.0f, 132.588f, 163.509f, 132.588f, 177.706f)
                lineTo(132.588f, 178.5f)
                curveTo(132.588f, 193.136f, 120.723f, 205.0f, 106.088f, 205.0f)
                lineTo(32.0f, 205.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
            ) {
                moveTo(32.0f, 205.0f)
                curveTo(19.2975f, 205.0f, 9.0f, 194.703f, 9.0f, 182.0f)
                lineTo(8.99998f, 32.0f)
                curveTo(8.99998f, 19.2975f, 19.2974f, 9.00001f, 32.0f, 9.00001f)
                lineTo(161.0f, 9.0f)
                curveTo(173.703f, 9.0f, 184.0f, 19.2975f, 184.0f, 32.0f)
                lineTo(184.0f, 126.294f)
                curveTo(184.0f, 140.491f, 172.491f, 152.0f, 158.294f, 152.0f)
                curveTo(144.097f, 152.0f, 132.588f, 163.509f, 132.588f, 177.706f)
                lineTo(132.588f, 178.5f)
                curveTo(132.588f, 193.136f, 120.723f, 205.0f, 106.088f, 205.0f)
                lineTo(32.0f, 205.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBDBDBD)),
            ) {
                moveTo(9.0f, 182.0f)
                lineTo(8.75f, 182.0f)
                lineTo(9.0f, 182.0f)
                close()
                moveTo(8.99998f, 32.0f)
                lineTo(8.74998f, 32.0f)
                lineTo(8.99998f, 32.0f)
                close()
                moveTo(32.0f, 205.0f)
                lineTo(32.0f, 204.75f)
                curveTo(19.4355f, 204.75f, 9.25f, 194.564f, 9.25f, 182.0f)
                lineTo(9.0f, 182.0f)
                lineTo(8.75f, 182.0f)
                curveTo(8.75f, 194.841f, 19.1594f, 205.25f, 32.0f, 205.25f)
                lineTo(32.0f, 205.0f)
                close()
                moveTo(9.0f, 182.0f)
                lineTo(9.25f, 182.0f)
                lineTo(9.24998f, 32.0f)
                lineTo(8.99998f, 32.0f)
                lineTo(8.74998f, 32.0f)
                lineTo(8.75f, 182.0f)
                lineTo(9.0f, 182.0f)
                close()
                moveTo(8.99998f, 32.0f)
                lineTo(9.24998f, 32.0f)
                curveTo(9.24998f, 19.4355f, 19.4355f, 9.25001f, 32.0f, 9.25001f)
                lineTo(32.0f, 9.00001f)
                lineTo(32.0f, 8.75001f)
                curveTo(19.1594f, 8.75001f, 8.74998f, 19.1594f, 8.74998f, 32.0f)
                lineTo(8.99998f, 32.0f)
                close()
                moveTo(32.0f, 9.00001f)
                lineTo(32.0f, 9.25001f)
                lineTo(161.0f, 9.25f)
                lineTo(161.0f, 9.0f)
                lineTo(161.0f, 8.75f)
                lineTo(32.0f, 8.75001f)
                lineTo(32.0f, 9.00001f)
                close()
                moveTo(161.0f, 9.0f)
                lineTo(161.0f, 9.25f)
                curveTo(173.564f, 9.25f, 183.75f, 19.4355f, 183.75f, 32.0f)
                lineTo(184.0f, 32.0f)
                lineTo(184.25f, 32.0f)
                curveTo(184.25f, 19.1594f, 173.841f, 8.75f, 161.0f, 8.75f)
                lineTo(161.0f, 9.0f)
                close()
                moveTo(184.0f, 32.0f)
                lineTo(183.75f, 32.0f)
                lineTo(183.75f, 126.294f)
                lineTo(184.0f, 126.294f)
                lineTo(184.25f, 126.294f)
                lineTo(184.25f, 32.0f)
                lineTo(184.0f, 32.0f)
                close()
                moveTo(132.588f, 177.706f)
                lineTo(132.338f, 177.706f)
                lineTo(132.338f, 178.5f)
                lineTo(132.588f, 178.5f)
                lineTo(132.838f, 178.5f)
                lineTo(132.838f, 177.706f)
                lineTo(132.588f, 177.706f)
                close()
                moveTo(106.088f, 205.0f)
                lineTo(106.088f, 204.75f)
                lineTo(32.0f, 204.75f)
                lineTo(32.0f, 205.0f)
                lineTo(32.0f, 205.25f)
                lineTo(106.088f, 205.25f)
                lineTo(106.088f, 205.0f)
                close()
                moveTo(132.588f, 178.5f)
                lineTo(132.338f, 178.5f)
                curveTo(132.338f, 192.997f, 120.585f, 204.75f, 106.088f, 204.75f)
                lineTo(106.088f, 205.0f)
                lineTo(106.088f, 205.25f)
                curveTo(120.862f, 205.25f, 132.838f, 193.274f, 132.838f, 178.5f)
                lineTo(132.588f, 178.5f)
                close()
                moveTo(158.294f, 152.0f)
                lineTo(158.294f, 151.75f)
                curveTo(143.959f, 151.75f, 132.338f, 163.371f, 132.338f, 177.706f)
                lineTo(132.588f, 177.706f)
                lineTo(132.838f, 177.706f)
                curveTo(132.838f, 163.647f, 144.235f, 152.25f, 158.294f, 152.25f)
                lineTo(158.294f, 152.0f)
                close()
                moveTo(184.0f, 126.294f)
                lineTo(183.75f, 126.294f)
                curveTo(183.75f, 140.353f, 172.353f, 151.75f, 158.294f, 151.75f)
                lineTo(158.294f, 152.0f)
                lineTo(158.294f, 152.25f)
                curveTo(172.629f, 152.25f, 184.25f, 140.629f, 184.25f, 126.294f)
                lineTo(184.0f, 126.294f)
                close()
            }
        }.build()
        return _MyIcon!!
    }

private var _MyIcon: ImageVector? = null

@Preview(showBackground = true)
@Composable
private fun MyIconPreview() {
    Box(
        modifier = Modifier
            .size(220.dp)
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberVectorPainter(image = MyIcon),
            contentDescription = "MyIcon preview",
            modifier = Modifier.size(193.dp, 214.dp)
        )
    }
}

