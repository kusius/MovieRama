package com.kusius.movies.core.designsystem.theme.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.kusius.movies.core.designsystem.theme.MovieRamaTheme

@Composable
fun MovieRamaSearchBar(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
) {
    var queryString by remember { mutableStateOf(searchQuery) }
    val focusManager = LocalFocusManager.current
    fun queryChanged(query: String) {
        queryString = query
        onQueryChanged(queryString)
    }

    fun clearQuery() = queryChanged("")

    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = queryString,
            onValueChange = ::queryChanged,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(queryString)
                    focusManager.clearFocus()
                }
            ),
            placeholder = placeholder,
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null)},
            trailingIcon = {
                if(queryString.isEmpty()) {
                    // no icon
                }
                else {
                    IconButton(onClick = ::clearQuery) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                }

            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchBar() {
    MovieRamaTheme() {
        MovieRamaSearchBar(
            searchQuery = "", onQueryChanged = {}, onSearch = {},
            placeholder = { Text(text = "Enter your search ") }
        )
    }
}