package dev.sanmer.mrepo.ui.screens.repository.view.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.database.entity.RepoEntity
import dev.sanmer.mrepo.model.online.TrackJson
import dev.sanmer.mrepo.ui.utils.expandedShape
import dev.sanmer.mrepo.utils.extensions.toDateTime

@Composable
fun TrackItem(
    tracks: List<Pair<RepoEntity, TrackJson>>
) = Box {
    var open by rememberSaveable { mutableStateOf(false) }

    TagItem(
        icon = R.drawable.tag,
        onClick = { open = true }
    )

    if (open) {
        ModalBottomSheet(
            onDismissRequest = { open = false },
            shape = BottomSheetDefaults.expandedShape(15.dp),
            windowInsets = WindowInsets.navigationBars
        ) {
            Text(
                text = stringResource(id = R.string.view_module_view_track),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            LazyColumn(
                modifier = Modifier
                    .padding(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = tracks,
                    key = { it.first.url }
                ) { (repo, track) ->
                    ValueItem(
                        repo = repo,
                        track = track
                    )
                }
            }
        }
    }
}

@Composable
private fun ValueItem(
    repo: RepoEntity,
    track: TrackJson
) = Surface(
    modifier = Modifier.fillMaxWidth(),
    tonalElevation = 6.dp,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    shape = RoundedCornerShape(15.dp)
) {
    Row(
        modifier = Modifier.padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(id = R.drawable.code_asterix),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = repo.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.view_module_type,
                    track.type.name.replace("_", " ")),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            stringResource(id = R.string.view_module_added,
                track.added.toDateTime()
            )

            Text(
                text = stringResource(id = R.string.view_module_added,
                    track.added.toDateTime()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}