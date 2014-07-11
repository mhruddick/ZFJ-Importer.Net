package com.thed.zfj.ui

import javax.swing.table._
import scala.swing._
import scala.swing.ListView._
import scala.swing.event._
import scala.collection.JavaConversions._
import com.thed.zfj.rest._
import com.thed.zfj.model._
import com.thed.model._
import com.thed.util._
import java.awt.Dimension
import com.thed.service.zie.ImportManager
import com.thed.service.impl.zie.TestcaseImportManagerImpl
import com.thed.service.TestLinkImporterManagerImpl
import actors.Actor
import org.apache.commons.logging.{ LogFactory, Log }
import actors.threadpool.{ Callable, Executors }
import java.util.{ AbstractMap, HashSet, HashMap }
import scala.Array
import java.io.File
import javax.swing.JFileChooser
import dispatch.classic.StatusCode
import com.thed.util.Discriminator

object ImportSwingApp extends SimpleSwingApplication {

  def top = new MainFrame() {
    title = "Zephyr Testcase Importer: Pick Import Type"
    val btExcel = new Button { text = "EXCEL" }
    val btXML = new Button { text = "XML" }
    var mainImportPanel: BaseImporter = null;
    preferredSize = new Dimension(800, 800);
    val bp = new BoxPanel(Orientation.Vertical) {
      contents += btExcel
      contents += btXML
    }
    contents = bp
    centerOnScreen()
    listenTo(btExcel, btXML)
    reactions += {
      case ButtonClicked(`btExcel`) =>
        bp.contents -= btExcel
        bp.contents -= btXML
        mainImportPanel = ExcelImporter
        listenTo(mainImportPanel.btBack)
        bp.contents += mainImportPanel
        deafTo(btExcel, btXML)
        top.pack
        mainImportPanel.revalidate()
        mainImportPanel.repaint()

      case ButtonClicked(`btXML`) =>
        bp.contents -= btExcel
        bp.contents -= btXML
        mainImportPanel = XMLImporter
        listenTo(mainImportPanel.btBack)
        bp.contents += mainImportPanel
        deafTo(btExcel, btXML)
        top.pack
        mainImportPanel.revalidate()
        mainImportPanel.repaint()
      case ButtonClicked(XMLImporter.btBack) =>
        goBack()
      case ButtonClicked(ExcelImporter.btBack) =>
        goBack()
    }

    def goBack(): Unit = {
      deafTo(mainImportPanel.btBack)
      bp.contents -= mainImportPanel
      mainImportPanel = null
      bp.contents += btExcel
      bp.contents += btXML
      listenTo(btExcel, btXML)
      bp.revalidate()
      bp.repaint()
      top.pack
      top.repaint()
    }
  }
}

object ExcelImporter extends BaseImporter {
  override def getImportManager(): ImportManager = new TestcaseImportManagerImpl();
  override def getImportFileChooser(): FileChooser = {
    new FileChooser(new java.io.File(".")) {
      fileFilter = new javax.swing.filechooser.FileNameExtensionFilter(".xls and .xlsx files", "xls", "xlsx")
    }
  }

  override def getTableColumns: List[String] = {
    return List("JIRA Field", "Excel Column")
  }

  override def addBaseFields(): Unit = {
    super.addBaseFields();
    Constants.fieldConfigs.putAll(Constants.excelFieldConfigs)
  }
}

object XMLImporter extends BaseImporter {
  excelFldPanel.visible = false
  override def getImportManager(): ImportManager = new TestLinkImporterManagerImpl();
  override def getImportFileChooser(): FileChooser = {
    new FileChooser(new java.io.File(".")) {
      fileFilter = new javax.swing.filechooser.FileNameExtensionFilter("Testlink .xml files", "xml")
    }
  }
  override def getTableColumns: List[String] = {
    return List("JIRA Field", "XML Column")
  }

  override def addBaseFields(): Unit = {
    super.addBaseFields();
    Constants.fieldConfigs.putAll(Constants.xmlFieldConfigs)
  }
}

abstract class BaseImporter extends FlowPanel {
  val btImport = new Button { text = "Start Import"; horizontalAlignment = Alignment.Center; enabled = false }
  val btBack = new Button { text = "<- Go Back"; horizontalAlignment = Alignment.Left }
  val btConnect = new Button { text = "Connect" }
  val status = new TextArea("", 3, 50)
  val tfUrl = new TextField("http://localhost:8080/rest", 20)
  val tfStartingRowNumber = new TextField("2", 5)
  val tfSheetFilter = new TextField(".*", 5)
  val btSave = new Button("Save")
  val btLoad = new Button("Load")
  val tfUserName = new TextField("admin", 5);
  val tfPassword = new PasswordField("admin", 5);
  val chkAttachFile = new CheckBox()
  val cbProjects = new ComboBox(List[Project](JiraService.dummyProject)) {
    renderer = Renderer(_.name)
    enabled = false
  }
  val cbissueType = new ComboBox(List[IssueType](JiraService.dummyIssueType)) {
    renderer = Renderer(_.name)
    enabled = false
  }
  var cbDiscriminator = new ComboBox(List[Pair[Discriminator, String]](
      Pair(Discriminator.BY_SHEET, "By Sheet"),
      Pair(Discriminator.BY_EMPTY_ROW, "By Empty Row"),
    Pair(Discriminator.BY_ID_CHANGE, "By ID Change"),
    Pair(Discriminator.BY_TESTCASE_NAME_CHANGE, "By Testcase name Change"))) {
    renderer = Renderer(_._2)
  }
  val excelFldPanel = new FlowPanel(FlowPanel.Alignment.Left)() {
    contents += new Label("Discriminator:")
    contents += cbDiscriminator
    contents += new Label("Starting Row #:")
    contents += tfStartingRowNumber
  }
  val chkImportAllSheets = new CheckBox()
  tfSheetFilter.enabled = false
  val excelFldPanel2 = new FlowPanel(FlowPanel.Alignment.Left)() {
    contents += new Label("Import all sheets:")
    contents += chkImportAllSheets
    contents += new Label("Sheet Filter:")
    contents += tfSheetFilter
  }
  
      
  val spreadSheet = new Spreadsheet(15, 2, getTableColumns)
  val importFileName = new TextField { columns = 25 }
  val importFileButton = new Button { text = "Pick Import File" }
  val importFolderButton = new Button { text = "Pick Import Folder" }
  contents += new BoxPanel(Orientation.Vertical) {
    contents += new FlowPanel {
      contents += new Label("Url: ")
      contents += tfUrl
      contents += new Label("username: ")
      contents += tfUserName
      contents += new Label("password: ")
      contents += tfPassword
      contents += btConnect
    }
    /*Project/IssueType selection panel*/
    contents += new FlowPanel(FlowPanel.Alignment.Left)(
      new Label("Project: "), cbProjects, new Label("Issue Type: "), cbissueType)
    contents += excelFldPanel
    contents += excelFldPanel2
    
   
    contents += new FlowPanel(FlowPanel.Alignment.Left)(
      new Label("Attach worksheet to issue"), chkAttachFile )
    contents += new FlowPanel(FlowPanel.Alignment.Left)(
      importFileName, importFileButton, importFolderButton /*, btSave, btLoad*/ )
    contents += spreadSheet
    contents += new FlowPanel(FlowPanel.Alignment.Center)(btBack, btImport)
    contents += new ScrollPane(status)
  }
  listenTo(btImport, btConnect, tfUserName, tfPassword, importFileButton,	importFolderButton, cbProjects.selection, cbissueType.selection, btSave, btLoad)
  listenTo(chkImportAllSheets)
  reactions += {
    case ButtonClicked(`btConnect`) =>
      JiraService.url_base = tfUrl.text
      JiraService.userName = tfUserName.text
      JiraService.passwd = tfPassword.password.mkString
      try {
	      val res = JiraService.getProjects();
	      cbProjects.peer.setModel(ComboBox.newConstantModel(res))
	      cbProjects.enabled = true;  
      } catch  {
        case e: StatusCode => {
          cbProjects.enabled = false;
        	Dialog.showMessage(this, "Unexpected response code: " + e.code, "Error fetching projects", Dialog.Message.Error)
        }
        case e: Exception => {
          cbProjects.enabled = false;
        	Dialog.showMessage(this, e.getMessage(), "Error fetching projects", Dialog.Message.Error)
        }
      }
      
    case EditDone(`tfUserName`) =>
      println(tfUserName.text)
    case EditDone(`tfPassword`) =>
    //println(tfPassword.password.deep.mkString)
    case SelectionChanged(`cbProjects`) =>
      println("Project changed " + cbProjects.selection.item.name)
      if (cbProjects.selection.item.id != "-1") {
        try {
	        var issueTypes = JiraService.getMeta(cbProjects.selection.item.id).get(0).issuetypes
	        issueTypes ::= JiraService.dummyIssueType
	        cbissueType.peer.setModel(ComboBox.newConstantModel(issueTypes))
	        cbissueType.enabled = true;
        } catch {
          case e: StatusCode => {
	          cbissueType.enabled = true;
	        	Dialog.showMessage(this, "Unexpected response code: " + e.code, "Error fetching issue types", Dialog.Message.Error)
	        }
	        case e: Exception => {
	          cbissueType.enabled = true;
	        	Dialog.showMessage(this, e.getMessage(), "Error fetching issue types", Dialog.Message.Error)
	        }
        }
      } else {
        cbissueType.peer.setModel(ComboBox.newConstantModel(List[IssueType](JiraService.dummyIssueType)))
        cbissueType.enabled = false;
      }

    case SelectionChanged(`cbissueType`) =>
      println("IssueType changed " + cbissueType.selection.item.name + cbProjects.selection.item.issuetypes)
      if (cbissueType.selection.item.id != "-1") {
        addBaseFields; addCustomFields
        println("Fields populated " + Constants.fieldConfigs)
        spreadSheet.tableModel.addAll(Constants.fieldConfigs)
        println("New Fields should show up in Table ")
        spreadSheet.table.revalidate()
      }

    case ButtonClicked(`importFileButton`) =>
      val configFileChooser = getImportFileChooser()
      if (configFileChooser.showOpenDialog(null) == FileChooser.Result.Approve) {
        importFileName.text = configFileChooser.selectedFile.getAbsolutePath
        btImport.enabled = true
      }
    case ButtonClicked(`importFolderButton`) =>
      val configFileChooser = new FileChooser(new File("."))
      configFileChooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
      
      if (configFileChooser.showOpenDialog(null) == FileChooser.Result.Approve) {
        importFileName.text = configFileChooser.selectedFile.getAbsolutePath
        btImport.enabled = true
      }
    case ButtonClicked(`chkImportAllSheets`) =>
      tfSheetFilter.enabled = chkImportAllSheets.selected
    case ButtonClicked(`btSave`) =>
      print("")
    case ButtonClicked(`btLoad`) =>
      print("")
    case ButtonClicked(`btImport`) =>
      status.text = ""
      var fieldMapDetails = new java.util.HashSet[FieldMapDetail]();
      for (data <- spreadSheet.tableModel.rowData) {
        fieldMapDetails.add(new FieldMapDetail(data(2).asInstanceOf[String], data(1).asInstanceOf[String]))
      }

      val fieldMap = new FieldMap(1l, "First Map", "description", new java.util.Date(), ".csv", tfStartingRowNumber.text.toInt, cbDiscriminator.selection.item._1, fieldMapDetails, "testcase")
      val jobHistories: HashSet[JobHistory] = new java.util.HashSet[JobHistory]() {
        val log = LogFactory.getLog(this.getClass)
        case class Append(msg: String)
        object Appender extends Actor {
          def act() {
            loop {
              react {
                case Append(msg) => log.debug(msg); status.text += msg; status.revalidate(); status.repaint()
              }
            }
          }
        }
        Appender.start()
        override def add(jb: JobHistory) = {
          val msg = jb.getActionDate + " " + jb.getComments + " \n"
          Appender ! Append(msg)
          super.add(jb);
        }
      }

      val importJob = new ImportJob(1l, "Temp", 
          importFileName.text, "csv", null, /*status*/ "1",
          null, fieldMap, jobHistories, "testcase", 
          Option(tfSheetFilter.text).filter( _ => chkImportAllSheets.selected) )
      importJob.setAttachFile(chkAttachFile.selected)
      JiraService.project = new Project(cbProjects.selection.item.id)
      JiraService.issueType = new IssueType(cbissueType.selection.item.id)
      val executor = Executors.newSingleThreadExecutor
      val result = executor.submit(new Callable {
        val log = LogFactory.getLog(this.getClass)
        def call = {
          JiraService.startImport(importJob, getImportManager())
        }
      })
      result.get()
  }

  def getTableColumns: List[String]

  /**
   * Adds customes field configurations to Constants, from where, they are accessed to populate UI as well to create customField data construct consumed to create Rest Request
   */
  def addCustomFields {
    cbissueType.selection.item.ensuring(_.fields != null).fields.foreach {
      entry =>
        val (fldName, fldVal) = entry;

        if (fldName.startsWith("customfield")) {
          val fieldMetadataId = populateFieldTypes(fldVal.asInstanceOf[Map[String, AnyRef]])
          //Map(required -> false, schema -> Map(type -> string, custom -> com.atlassian.jira.plugin.system.customfieldtypes:textfield, customId -> 10100), name -> P, operations -> List(set))
          var desc = fldVal.asInstanceOf[Map[String, String]].get("name").get.toString
          //val datatype = fldVal.asInstanceOf[Map[String, AnyRef]].get("schema").get.asInstanceOf[Map[String, AnyRef]].get("type").get.toString
          val required = (fldVal.asInstanceOf[Map[String, AnyRef]].get("required").get.asInstanceOf[Boolean])
          if (required) desc += " *"
          val allowedValuesArray = fldVal.asInstanceOf[Map[String, AnyRef]].get("allowedValues") match {
            case Some(valueMap) => valueMap.asInstanceOf[List[AnyRef]]
            case None => List()
          }
          if (!desc.contains("Zephyr"))
            Constants.fieldConfigs.put(fldName, new FieldConfig(fldName, "testcase", false, fieldMetadataId, fldName, fldName, desc, "This is " + desc, required, true, true, true, 255, allowedValuesArray))
        }
    }
  }

  /**
   * Creates FieldTypeMetadata and stores it in <code>#Constants</code>
   * Called every time when issueType Changes
   * @param fldVal
   * @return fieldMetaDataId which is concatenation of jiraDataType and itemDataType (only populated if jiraDataType is Array)
   */
  def populateFieldTypes(fldVal: Map[String, AnyRef]): String = {
    val customFldMetadata = fldVal.get("schema").get.asInstanceOf[Map[String, AnyRef]]

    val jiraDataType = customFldMetadata.get("type").get.toString
    val itemsDataType = {
      if (jiraDataType == "array" && customFldMetadata.contains("items")) {
        customFldMetadata.get("items").get.toString
      } else { "" }
    }
    val fieldMetadataId: String = jiraDataType + ":" + itemsDataType
    val fieldMetadata = new FieldTypeMetadata(fieldMetadataId, "Text (1024)", jiraDataType, itemsDataType, 1024, true, 100)
    Constants.fieldTypeMetadataMap.put(fieldMetadataId, fieldMetadata)
    fieldMetadataId
  }

  def addBaseFields(): Unit = {
    Constants.fieldConfigs.clear()
  }
  def getImportManager(): ImportManager
  def getImportFileChooser(): FileChooser
}

class Spreadsheet(val height: Int, val width: Int, tableColumns: List[String]) extends ScrollPane {
  val tableModel = new MyTableModel(Array[Array[Any]](), tableColumns)
  val table = new Table(height, width) {
    model = tableModel
    rowHeight = 25
    autoResizeMode = Table.AutoResizeMode.LastColumn
    showGrid = true
    gridColor = new java.awt.Color(150, 150, 150)
  }
  //for ( (fieldId, field) <- Constants.fieldConfigs ) { println(field.getDisplayName()); tableModel.addRow( Array[AnyRef](field.getDisplayName(), "", fieldId) ) }

  val rowHeader = new ListView((0 until Constants.fieldConfigs.size()) map (_.toString)) {
    fixedCellWidth = 30
    fixedCellHeight = table.rowHeight
  }
  //for (row <- cells; cell <- row) listenTo(cell)
  viewportView = table
  rowHeaderView = rowHeader
}

class MyTableModel(var rowData: Array[Array[Any]], var columnNames: Seq[String]) extends AbstractTableModel {
  override def getColumnName(column: Int) = columnNames(column).toString
  def getRowCount() = rowData.length
  def getColumnCount() = columnNames.length
  def getValueAt(row: Int, col: Int): AnyRef = rowData(row)(col).asInstanceOf[AnyRef]
  override def isCellEditable(row: Int, column: Int) = (column == 1)
  override def setValueAt(value: Any, row: Int, col: Int) {
    rowData(row)(col) = value
  }
  def addRow(data: Array[AnyRef]) {
    rowData ++= Array(data.asInstanceOf[Array[Any]])
  }

  def addAll(data: HashMap[String, FieldConfig]) {
    rowData = Array()
    data.foreach { entry => val (fieldId, field) = entry; addRow(Array[AnyRef](field.getDisplayName(), "", fieldId)) }
  }
}
