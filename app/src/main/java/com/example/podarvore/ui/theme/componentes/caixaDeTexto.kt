package com.example.podarvore.ui.theme.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun caixaDeTexto(tipo: String,
                 textoemcima: String,
                 icone: Boolean,
                 value: String,
                 onValueChange: (String) -> Unit,
                 clique: () -> Unit = {}) {
    val icone_final = Icons.Filled.ArrowForward

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
        if (textoemcima.isNotEmpty()) {
            Text(text = textoemcima, fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = tipo, color = Color.Gray, fontSize = 19.sp) },
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            shape = RoundedCornerShape(15.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .border(color = Color(0xffB4B4B4), width = 3.dp, shape = RoundedCornerShape(15.dp)),
            trailingIcon = if (icone) {
                {
                    IconButton(onClick = {
                        clique()
                    }) {
                        Icon(
                            imageVector = icone_final,
                            contentDescription = null
                        )
                    }
                }
            } else {
                null
            }
        )
    }
}
