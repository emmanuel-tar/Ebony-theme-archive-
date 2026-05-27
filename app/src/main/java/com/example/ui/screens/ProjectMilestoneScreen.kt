package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProjectMilestone
import com.example.viewmodel.EbonyViewModel

@Composable
fun ProjectMilestoneScreen(
    viewModel: EbonyViewModel,
    modifier: Modifier = Modifier
) {
    val milestones by viewModel.milestones.collectAsState()
    val baseFee by viewModel.baseDesignFee.collectAsState()

    var activeMilestoneIdForEdit by remember { mutableStateOf<String?>("draft_review") }
    var userFeedbackText by remember { mutableStateOf("") }
    var isApprovedCheck by remember { mutableStateOf(false) }

    // Synchronize editing variables when the selected milestone changes
    LaunchedEffect(activeMilestoneIdForEdit, milestones) {
        val selected = milestones.firstOrNull { it.milestoneId == activeMilestoneIdForEdit }
        if (selected != null) {
            userFeedbackText = selected.feedback
            isApprovedCheck = selected.isApproved
        }
    }

    // Calculations based on 30% / 40% / 30% splits
    val upfrontAmount = baseFee * 0.30
    val draftAmount = baseFee * 0.40
    val finalAmount = baseFee * 0.30

    var earnedAmount = 0.0
    milestones.forEach { m ->
        if (m.isApproved) {
            earnedAmount += when (m.milestoneId) {
                "upfront" -> upfrontAmount
                "draft_review" -> draftAmount
                "final_review" -> finalAmount
                else -> 0.0
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Milestone & Budget Title
        item {
            Column {
                Text(
                    text = "OBLIGATIONS & INVOICES",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    letterSpacing = 1.8.sp
                )
                Text(
                    text = "Fee Splitting & Milestone Reviews",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Adjustable Design Fee & Interactive Live Split Calculator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Interactive Design Invoice Splits (30% / 40% / 30%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Standard Design Fee:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$${baseFee}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Incremental sliders / buttons for fee adjustment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(1500, 2500, 3500, 5000).forEach { feeOption ->
                            val isSelected = baseFee == feeOption
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .clickable { viewModel.setBaseDesignFee(feeOption) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$${feeOption}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )

                    // Display Calculations
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SplitRowItem(label = "Upfront Retainer (30%)", value = upfrontAmount, isCleared = milestones.firstOrNull { it.milestoneId == "upfront" }?.isApproved ?: false)
                        SplitRowItem(label = "Concept Draft Review (40%)", value = draftAmount, isCleared = milestones.firstOrNull { it.milestoneId == "draft_review" }?.isApproved ?: false)
                        SplitRowItem(label = "Final Delivery (30%)", value = finalAmount, isCleared = milestones.firstOrNull { it.milestoneId == "final_review" }?.isApproved ?: false)
                    }

                    // Total Cleared Budget styled in PrimaryContainer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Earned / Cleared Fee:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$${earnedAmount.toInt()} / $${baseFee}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // List of Active Milestones to Edit
        item {
            Text(
                text = "SUBMISSION REVIEWS & FEEDBACK",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                letterSpacing = 1.8.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(milestones) { milestone ->
            val isSelectedForEdit = milestone.milestoneId == activeMilestoneIdForEdit
            val activeContainerBg = if (isSelectedForEdit) MaterialTheme.colorScheme.primaryContainer else Color.White
            val activeBorderColor = if (isSelectedForEdit) MaterialTheme.colorScheme.primary else Color.Transparent

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("milestone_card_${milestone.milestoneId}")
                    .border(
                        width = if (isSelectedForEdit) 2.dp else 0.dp,
                        color = activeBorderColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { activeMilestoneIdForEdit = milestone.milestoneId },
                colors = CardDefaults.cardColors(containerColor = activeContainerBg),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelectedForEdit) 0.dp else 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = milestone.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelectedForEdit) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = if (milestone.isApproved) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = "Approval check",
                            tint = if (milestone.isApproved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = milestone.feedback,
                        fontSize = 12.sp,
                        color = if (isSelectedForEdit) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Feedback Editor Workspace Card for the highlighted milestone
        activeMilestoneIdForEdit?.let { currentEditId ->
            val correspondingMilestone = milestones.firstOrNull { it.milestoneId == currentEditId }
            if (correspondingMilestone != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("milestone_edit_form")
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Feedback Editor Workspace",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Editing feedback for: ${correspondingMilestone.title}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Status switcher
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isApprovedCheck = !isApprovedCheck }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isApprovedCheck,
                                    onCheckedChange = { isApprovedCheck = it },
                                    modifier = Modifier.testTag("milestone_approval_checkbox"),
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = "Mark Stage as Client Approved",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Feedback Text Area
                            OutlinedTextField(
                                value = userFeedbackText,
                                onValueChange = { userFeedbackText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("milestone_feedback_input")
                                    .height(100.dp),
                                label = { Text("Client Comments & Revisions Specification...", fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                                )
                            )

                            // Control buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.approveMilestone(currentEditId, userFeedbackText)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("save_feedback_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                TextButton(
                                    onClick = { viewModel.resetMilestones() },
                                    modifier = Modifier.testTag("reset_milestones_button")
                                ) {
                                    Text("Reset Specs", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplitRowItem(
    label: String,
    value: Double,
    isCleared: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = if (isCleared) Icons.Default.CheckCircle else Icons.Default.Info,
                contentDescription = null,
                tint = if (isCleared) MaterialTheme.colorScheme.primary else Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "$${value.toInt()}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCleared) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}
