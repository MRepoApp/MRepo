package com.sanmer.mrepo.ui.page.apptheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.utils.Logo

@Composable
fun ExampleItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedCard(
            modifier = Modifier
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Logo(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .size(20.dp),
                        iconRes = R.drawable.ic_logo
                    )

                    Text(text = stringResource(id = R.string.app_name))
                }

                Card(
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(0.9f)
                    )
                }

                Card(
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(160.dp))

                Spacer(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(0.45f)
                        )
                        .height(45.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}