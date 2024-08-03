package com.example.jetpackcompose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoApp()
                }
            }
        }
    }
}

@Composable
fun EditTodoDialog(
    item: TodoItem,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var newTitle by remember { mutableStateOf(item.title) }
    var newDescription by remember { mutableStateOf(item.description) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Editar nota") },
        text = {
            Column {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = newDescription,
                    onValueChange = { newDescription = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(newTitle, newDescription)
                onDismiss()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ConfirmDeleteDialog(
    item: TodoItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Eliminar Nota") },
        text = { Text("Esta seguro de querer eliminar la nota?") },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Si")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("No")
            }
        }
    )
}


@SuppressLint("AutoboxingStateCreation")
@Composable
fun TodoApp() {
    var todoItems by remember { mutableStateOf(listOf<TodoItem>()) }
    var currentId by remember { mutableStateOf(0) }
    var itemToEdit by remember { mutableStateOf<TodoItem?>(null) }
    var itemToDelete by remember { mutableStateOf<TodoItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)

    ) {
        TodoInput { title, description ->
            todoItems = todoItems + TodoItem(currentId++, title, description)
        }
        Spacer(modifier = Modifier.height(20.dp))
        TodoList(
            items = todoItems,
            onToggleDone = { item ->
                todoItems = todoItems.map {
                    if (it.id == item.id) it.copy(isDone = !it.isDone) else it
                }
            },
            onEditItem = { item ->
                itemToEdit = item
            },
            onDeleteItem = { item ->
                itemToDelete = item
            }
        )

        itemToEdit?.let { item ->
            EditTodoDialog(
                item = item,
                onDismiss = { itemToEdit = null },
                onConfirm = { newTitle, newDescription ->
                    todoItems = todoItems.map {
                        if (it.id == item.id) it.copy(title = newTitle, description = newDescription) else it
                    }
                }
            )
        }

        itemToDelete?.let { item ->
            ConfirmDeleteDialog(
                item = item,
                onDismiss = { itemToDelete = null },
                onConfirm = {
                    todoItems = todoItems.filter { it.id != item.id }
                }
            )
        }
    }
}



@Composable
fun TodoList(items: List<TodoItem>, onToggleDone: (TodoItem) -> Unit, onEditItem: (TodoItem) -> Unit, onDeleteItem: (TodoItem) -> Unit) {
    LazyColumn {
        items(items) { item ->
            TodoRow(item, onToggleDone, onEditItem, onDeleteItem)
        }
    }
}

@Composable
fun TodoRow(item: TodoItem, onToggleDone: (TodoItem) -> Unit, onEditItem: (TodoItem) -> Unit, onDeleteItem: (TodoItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Checkbox(
            checked = item.isDone,
            onCheckedChange = { onToggleDone(item) }
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.width(7.dp))
        IconButton(onClick = { onEditItem(item) }) {
            Icon(Icons.Default.Edit, contentDescription = "Editar")
        }
        IconButton(onClick = { onDeleteItem(item) }) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
        }
    }
}

@Composable
fun TodoInput(onAdd: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(7.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(7.dp))
        Button(
            onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
                    onAdd(title, description)
                    title = ""
                    description = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            //change 1
        ) {
            Text("Agregar Nota")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackComposeTheme {
        TodoApp()
    }
}

