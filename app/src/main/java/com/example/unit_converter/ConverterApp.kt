package com.example.unit_converter

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unit_converter.ui.theme.BlueBackground
import com.example.unit_converter.ui.theme.BluePrimary
import com.example.unit_converter.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterApp() {
    val softBlueGradient = Brush.verticalGradient(
        colors = listOf(BlueBackground, White)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = softBlueGradient)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopHeader()
        Spacer(modifier = Modifier.height(32.dp))

        var selectedConversion by remember { mutableStateOf("Length") }
        val conversionTypes = listOf("Length", "Weight", "Temperature")
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedConversion,
                onValueChange = {},
                readOnly = true,
                label = { Text("What do you want to convert?") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                conversionTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedConversion = type
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Crossfade(targetState = selectedConversion, label = "") {
                    when (it) {
                        "Length" -> LengthConverter()
                        "Weight" -> WeightConverter()
                        "Temperature" -> TemperatureConverter()
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeader() {
    Text(
        text = "Unit Converter",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

// Common Dropdown
@Composable
fun <T> SimpleDropdown(selected: T, options: List<T>, onSelect: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, shape = RoundedCornerShape(8.dp)) {
            Text(selected.toString())
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt.toString()) }, onClick = { onSelect(opt); expanded = false })
            }
        }
    }
}

@Composable
fun ConverterSection(units: List<String>, convert: (Double, String, String) -> Double, label: String) {
    var from by remember { mutableStateOf(units[0]) }
    var to by remember { mutableStateOf(units[1]) }
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SimpleDropdown(selected = from, options = units, onSelect = { from = it })
            IconButton(onClick = { val t = from; from = to; to = t }) {
                Icon(Icons.Default.SwapHoriz, contentDescription = "swap", tint = BluePrimary)
            }
            SimpleDropdown(selected = to, options = units, onSelect = { to = it })
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                result = try {
                    val v = input.toDoubleOrNull() ?: 0.0
                    convert(v, from, to).toString()
                } catch (_: Exception) { "Error" }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) { Text("Convert", fontSize = 18.sp) }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Result: $result", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium)
    }
}

// LENGTH CONVERTER
@Composable
fun LengthConverter() {
    ConverterSection(
        units = listOf("Meter", "Kilometer", "Centimeter", "Millimeter", "Mile", "Yard", "Foot", "Inch"),
        convert = ::convertLength,
        label = "Enter length"
    )
}

fun convertLength(value: Double, from: String, to: String): Double {
    // convert input to meters first
    val toMeters = when (from) {
        "Kilometer" -> value * 1000.0
        "Centimeter" -> value / 100.0
        "Millimeter" -> value / 1000.0
        "Mile" -> value * 1609.344
        "Yard" -> value * 0.9144
        "Foot" -> value * 0.3048
        "Inch" -> value * 0.0254
        else -> value // Meter
    }
    return when (to) {
        "Kilometer" -> toMeters / 1000.0
        "Centimeter" -> toMeters * 100.0
        "Millimeter" -> toMeters * 1000.0
        "Mile" -> toMeters / 1609.344
        "Yard" -> toMeters / 0.9144
        "Foot" -> toMeters / 0.3048
        "Inch" -> toMeters / 0.0254
        else -> toMeters
    }
}

// WEIGHT CONVERTER
@Composable
fun WeightConverter() {
    ConverterSection(
        units = listOf("Kilogram", "Gram", "Milligram", "Pound", "Ounce"),
        convert = ::convertWeight,
        label = "Enter weight"
    )
}

fun convertWeight(value: Double, from: String, to: String): Double {
    // convert to kilograms first
    val toKg = when (from) {
        "Gram" -> value / 1000.0
        "Milligram" -> value / 1_000_000.0
        "Pound" -> value * 0.45359237
        "Ounce" -> value * 0.028349523125
        else -> value // Kilogram
    }
    return when (to) {
        "Gram" -> toKg * 1000.0
        "Milligram" -> toKg * 1_000_000.0
        "Pound" -> toKg / 0.45359237
        "Ounce" -> toKg / 0.028349523125
        else -> toKg
    }
}

// TEMPERATURE CONVERTER
@Composable
fun TemperatureConverter() {
    ConverterSection(
        units = listOf("Celsius", "Fahrenheit", "Kelvin"),
        convert = ::convertTemperature,
        label = "Enter temperature"
    )
}

fun convertTemperature(value: Double, from: String, to: String): Double {
    // convert to Celsius first
    val toC = when (from) {
        "Fahrenheit" -> (value - 32.0) * 5.0 / 9.0
        "Kelvin" -> value - 273.15
        else -> value
    }
    return when (to) {
        "Fahrenheit" -> toC * 9.0 / 5.0 + 32.0
        "Kelvin" -> toC + 273.15
        else -> toC
    }
}