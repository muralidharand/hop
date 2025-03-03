/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.pipeline.transforms.creditcardvalidator;

import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.ui.core.PropsUi;
import org.apache.hop.ui.core.dialog.BaseDialog;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.apache.hop.ui.pipeline.transform.ComponentSelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CreditCardValidatorDialog extends BaseTransformDialog {
  private static final Class<?> PKG = CreditCardValidatorMeta.class;

  private boolean gotPreviousFields = false;

  private CCombo wFieldName;

  private TextVar wResult;
  private TextVar wFileType;

  private TextVar wNotValidMsg;

  private Button wgetOnlyDigits;

  private final CreditCardValidatorMeta input;

  public CreditCardValidatorDialog(
      Shell parent,
      IVariables variables,
      CreditCardValidatorMeta transformMeta,
      PipelineMeta pipelineMeta) {
    super(parent, variables, transformMeta, pipelineMeta);
    input = transformMeta;
  }

  @Override
  public String open() {
    Shell parent = getParent();

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
    PropsUi.setLook(shell);
    setShellImage(shell, input);

    ModifyListener lsMod = e -> input.setChanged();

    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = PropsUi.getFormMargin();
    formLayout.marginHeight = PropsUi.getFormMargin();

    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "CreditCardValidatorDialog.Shell.Title"));

    int middle = props.getMiddlePct();
    int margin = PropsUi.getMargin();

    // TransformName line
    wlTransformName = new Label(shell, SWT.RIGHT);
    wlTransformName.setText(
        BaseMessages.getString(PKG, "CreditCardValidatorDialog.TransformName.Label"));
    PropsUi.setLook(wlTransformName);
    fdlTransformName = new FormData();
    fdlTransformName.left = new FormAttachment(0, 0);
    fdlTransformName.right = new FormAttachment(middle, -margin);
    fdlTransformName.top = new FormAttachment(0, margin);
    wlTransformName.setLayoutData(fdlTransformName);
    wTransformName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wTransformName.setText(transformName);
    PropsUi.setLook(wTransformName);
    wTransformName.addModifyListener(lsMod);
    fdTransformName = new FormData();
    fdTransformName.left = new FormAttachment(middle, 0);
    fdTransformName.top = new FormAttachment(0, margin);
    fdTransformName.right = new FormAttachment(100, 0);
    wTransformName.setLayoutData(fdTransformName);

    // filename field
    Label wlFieldName = new Label(shell, SWT.RIGHT);
    wlFieldName.setText(BaseMessages.getString(PKG, "CreditCardValidatorDialog.FieldName.Label"));
    PropsUi.setLook(wlFieldName);
    FormData fdlFieldName = new FormData();
    fdlFieldName.left = new FormAttachment(0, 0);
    fdlFieldName.right = new FormAttachment(middle, -margin);
    fdlFieldName.top = new FormAttachment(wTransformName, margin);
    wlFieldName.setLayoutData(fdlFieldName);

    wFieldName = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
    PropsUi.setLook(wFieldName);
    wFieldName.addModifyListener(lsMod);
    FormData fdFieldName = new FormData();
    fdFieldName.left = new FormAttachment(middle, 0);
    fdFieldName.top = new FormAttachment(wTransformName, margin);
    fdFieldName.right = new FormAttachment(100, 0);
    wFieldName.setLayoutData(fdFieldName);
    wFieldName.addFocusListener(
        new FocusListener() {
          @Override
          public void focusLost(FocusEvent e) {
            // Disable focuslost
          }

          @Override
          public void focusGained(FocusEvent e) {
            Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
            shell.setCursor(busy);
            get();
            shell.setCursor(null);
            busy.dispose();
          }
        });

    // get only digits?
    Label wlgetOnlyDigits = new Label(shell, SWT.RIGHT);
    wlgetOnlyDigits.setText(BaseMessages.getString(PKG, "CreditCardValidator.getOnlyDigits.Label"));
    PropsUi.setLook(wlgetOnlyDigits);
    FormData fdlgetOnlyDigits = new FormData();
    fdlgetOnlyDigits.left = new FormAttachment(0, 0);
    fdlgetOnlyDigits.top = new FormAttachment(wFieldName, margin);
    fdlgetOnlyDigits.right = new FormAttachment(middle, -margin);
    wlgetOnlyDigits.setLayoutData(fdlgetOnlyDigits);
    wgetOnlyDigits = new Button(shell, SWT.CHECK);
    PropsUi.setLook(wgetOnlyDigits);
    wgetOnlyDigits.setToolTipText(
        BaseMessages.getString(PKG, "CreditCardValidator.getOnlyDigits.Tooltip"));
    FormData fdgetOnlyDigits = new FormData();
    fdgetOnlyDigits.left = new FormAttachment(middle, 0);
    fdgetOnlyDigits.top = new FormAttachment(wFieldName, margin);
    wgetOnlyDigits.setLayoutData(fdgetOnlyDigits);
    wgetOnlyDigits.addSelectionListener(new ComponentSelectionListener(input));

    // ///////////////////////////////
    // START OF Output Fields GROUP //
    // ///////////////////////////////

    Group wOutputFields = new Group(shell, SWT.SHADOW_NONE);
    PropsUi.setLook(wOutputFields);
    wOutputFields.setText(
        BaseMessages.getString(PKG, "CreditCardValidatorDialog.OutputFields.Label"));

    FormLayout outputFieldsgroupLayout = new FormLayout();
    outputFieldsgroupLayout.marginWidth = 10;
    outputFieldsgroupLayout.marginHeight = 10;
    wOutputFields.setLayout(outputFieldsgroupLayout);

    // Result fieldname ...
    Label wlResult = new Label(wOutputFields, SWT.RIGHT);
    wlResult.setText(BaseMessages.getString(PKG, "CreditCardValidatorDialog.ResultField.Label"));
    PropsUi.setLook(wlResult);
    FormData fdlResult = new FormData();
    fdlResult.left = new FormAttachment(0, -margin);
    fdlResult.right = new FormAttachment(middle, -2 * margin);
    fdlResult.top = new FormAttachment(wgetOnlyDigits, 2 * margin);
    wlResult.setLayoutData(fdlResult);

    wResult = new TextVar(variables, wOutputFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wResult.setToolTipText(
        BaseMessages.getString(PKG, "CreditCardValidatorDialog.ResultField.Tooltip"));
    PropsUi.setLook(wResult);
    wResult.addModifyListener(lsMod);
    FormData fdResult = new FormData();
    fdResult.left = new FormAttachment(middle, -margin);
    fdResult.top = new FormAttachment(wgetOnlyDigits, 2 * margin);
    fdResult.right = new FormAttachment(100, 0);
    wResult.setLayoutData(fdResult);

    // FileType fieldname ...
    Label wlCardType = new Label(wOutputFields, SWT.RIGHT);
    wlCardType.setText(BaseMessages.getString(PKG, "CreditCardValidatorDialog.CardType.Label"));
    PropsUi.setLook(wlCardType);
    FormData fdlCardType = new FormData();
    fdlCardType.left = new FormAttachment(0, -margin);
    fdlCardType.right = new FormAttachment(middle, -2 * margin);
    fdlCardType.top = new FormAttachment(wResult, margin);
    wlCardType.setLayoutData(fdlCardType);

    wFileType = new TextVar(variables, wOutputFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wFileType.setToolTipText(
        BaseMessages.getString(PKG, "CreditCardValidatorDialog.CardType.Tooltip"));
    PropsUi.setLook(wFileType);
    wFileType.addModifyListener(lsMod);
    FormData fdCardType = new FormData();
    fdCardType.left = new FormAttachment(middle, -margin);
    fdCardType.top = new FormAttachment(wResult, margin);
    fdCardType.right = new FormAttachment(100, 0);
    wFileType.setLayoutData(fdCardType);

    // UnvalidMsg fieldname ...
    Label wlNotValidMsg = new Label(wOutputFields, SWT.RIGHT);
    wlNotValidMsg.setText(
        BaseMessages.getString(PKG, "CreditCardValidatorDialog.NotValidMsg.Label"));
    PropsUi.setLook(wlNotValidMsg);
    FormData fdlNotValidMsg = new FormData();
    fdlNotValidMsg.left = new FormAttachment(0, -margin);
    fdlNotValidMsg.right = new FormAttachment(middle, -2 * margin);
    fdlNotValidMsg.top = new FormAttachment(wFileType, margin);
    wlNotValidMsg.setLayoutData(fdlNotValidMsg);

    wNotValidMsg = new TextVar(variables, wOutputFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wNotValidMsg.setToolTipText(
        BaseMessages.getString(PKG, "CreditCardValidatorDialog.NotValidMsg.Tooltip"));
    PropsUi.setLook(wNotValidMsg);
    wNotValidMsg.addModifyListener(lsMod);
    FormData fdNotValidMsg = new FormData();
    fdNotValidMsg.left = new FormAttachment(middle, -margin);
    fdNotValidMsg.top = new FormAttachment(wFileType, margin);
    fdNotValidMsg.right = new FormAttachment(100, 0);
    wNotValidMsg.setLayoutData(fdNotValidMsg);

    FormData fdAdditionalFields = new FormData();
    fdAdditionalFields.left = new FormAttachment(0, margin);
    fdAdditionalFields.top = new FormAttachment(wgetOnlyDigits, 2 * margin);
    fdAdditionalFields.right = new FormAttachment(100, -margin);
    wOutputFields.setLayoutData(fdAdditionalFields);

    // ///////////////////////////////
    // END OF Additional Fields GROUP //
    // ///////////////////////////////

    // THE BUTTONS
    wOk = new Button(shell, SWT.PUSH);
    wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
    wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

    setButtonPositions(new Button[] {wOk, wCancel}, margin, wOutputFields);

    // Add listeners
    wOk.addListener(SWT.Selection, e -> ok());
    wCancel.addListener(SWT.Selection, e -> cancel());

    getData();

    BaseDialog.defaultShellHandling(shell, c -> ok(), c -> cancel());

    return transformName;
  }

  /** Copy information from the meta-data input to the dialog fields. */
  public void getData() {

    wFieldName.setText(Const.NVL(input.getFieldName(), ""));
    wgetOnlyDigits.setSelection(input.isOnlyDigits());
    wResult.setText(Const.NVL(input.getResultFieldName(), ""));
    wFileType.setText(Const.NVL(input.getCardType(), ""));
    wNotValidMsg.setText(Const.NVL(input.getNotValidMessage(), ""));

    wTransformName.selectAll();
    wTransformName.setFocus();
  }

  private void cancel() {
    transformName = null;
    input.setChanged(changed);
    dispose();
  }

  private void ok() {
    if (Utils.isEmpty(wTransformName.getText())) {
      return;
    }
    input.setFieldName(wFieldName.getText());
    input.setOnlyDigits(wgetOnlyDigits.getSelection());
    input.setResultFieldName(wResult.getText());
    input.setCardType(wFileType.getText());
    input.setNotValidMessage(wNotValidMsg.getText());
    transformName = wTransformName.getText(); // return value

    dispose();
  }

  private void get() {
    if (!gotPreviousFields) {
      try {
        String columnName = wFieldName.getText();
        wFieldName.removeAll();
        IRowMeta r = pipelineMeta.getPrevTransformFields(variables, transformName);
        if (r != null) {
          r.getFieldNames();

          for (int i = 0; i < r.getFieldNames().length; i++) {
            wFieldName.add(r.getFieldNames()[i]);
          }
        }
        wFieldName.setText(columnName);
        gotPreviousFields = true;
      } catch (HopException ke) {
        new ErrorDialog(
            shell,
            BaseMessages.getString(PKG, "CreditCardValidatorDialog.FailedToGetFields.DialogTitle"),
            BaseMessages.getString(
                PKG, "CreditCardValidatorDialog.FailedToGetFields.DialogMessage"),
            ke);
      }
    }
  }
}
