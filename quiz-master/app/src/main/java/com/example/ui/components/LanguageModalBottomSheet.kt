package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.LocalizationDictionary
import com.example.data.SupportedLanguages
import com.example.ui.screens.LanguageCardItem
import com.example.ui.theme.NaturalAccentGold
import com.example.ui.theme.NaturalBackgroundLight
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalSurfaceLight
import com.example.ui.theme.NaturalTextPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageModalBottomSheet(
    currentLanguageCode: String,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val strings = LocalizationDictionary.getStrings(currentLanguageCode)

    val filteredLanguages = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            SupportedLanguages.languages
        } else {
            SupportedLanguages.languages.filter {
                it.nativeName.contains(searchQuery, ignoreCase = true) ||
                        it.countryNameNative.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = NaturalBackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = strings.switchLanguage,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = NaturalTextPrimaryLight
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(text = strings.searchLanguagePlaceholder) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("modal_search_language"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NaturalAccentGold,
                    unfocusedBorderColor = NaturalBorderLight,
                    focusedContainerColor = NaturalSurfaceLight,
                    unfocusedContainerColor = NaturalSurfaceLight
                )
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                items(filteredLanguages, key = { it.code }) { lang ->
                    val isSelected = currentLanguageCode.equals(lang.code, ignoreCase = true)
                    LanguageCardItem(
                        language = lang,
                        isSelected = isSelected,
                        onSelect = {
                            onLanguageSelected(lang)
                            onDismissRequest()
                        }
                    )
                }
            }
        }
    }
}
