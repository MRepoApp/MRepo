package com.sanmer.mrepo.ui.screens.viewmodule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.activity.license.LicenseActivity
import com.sanmer.mrepo.ui.component.NormalChip
import com.sanmer.mrepo.viewmodel.DetailViewModel

@Composable
fun ModuleInfoItem(
    viewModel: DetailViewModel = viewModel()
) {
    val context = LocalContext.current

    OutlinedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewModel.module.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.module_author, viewModel.module.author),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text =viewModel.module.description,
                style = MaterialTheme.typography.bodySmall
            )

            if (viewModel.hasLabel) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    if (viewModel.isMulti) {
                        NormalChip(
                            painter = painterResource(id = R.drawable.hierarchy_outline),
                            text = "MULTIREPO"
                        )
                    }

                    if (viewModel.hasLicense) {
                        NormalChip(
                            painter = painterResource(id = R.drawable.document_text_outline),
                            text = viewModel.module.license,
                            enabled = viewModel.module.license != "UNKNOWN",
                            onClick = {
                                LicenseActivity.start(
                                    context = context,
                                    licenseId = viewModel.module.license
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}