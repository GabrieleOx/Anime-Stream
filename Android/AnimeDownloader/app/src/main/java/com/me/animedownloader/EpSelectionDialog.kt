package com.me.animedownloader

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SelectionDialog(
    onDismiss:() -> Unit,
    onConfirm:(Int) -> Unit,
    contesto: Context
) {
    var episodio by remember {
        mutableIntStateOf(0)
    }
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card (
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .border(border = BorderStroke(2.dp, Color(84, 79, 79, 255)), shape = RoundedCornerShape(15.dp))
        ) {
            Column (
                modifier = Modifier
                    .background(Color(183, 182, 182, 255))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Text(
                        text = "Inserisci il numero dell'episodio che stai\ncercando per cercare solo quelli vicini oppure\n0 per usare l'automatico:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Image(
                        painter = painterResource(id = R.drawable.alert),
                        contentDescription = null
                    )
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = episodio.toString(),
                        onValueChange = { text ->
                            try {
                                episodio = text.toInt()
                            } catch (_: Exception) {
                                Toast.makeText(contesto, "Valore non valido...", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.width(250.dp)
                    )
                    Button(
                        onClick = { onConfirm(episodio) },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "Conferma"
                        )
                    }
                }
            }
        }
    }
}