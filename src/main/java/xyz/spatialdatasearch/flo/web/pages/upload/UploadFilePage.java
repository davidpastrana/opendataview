package com.spatialdatasearch.at.web.pages.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spatialdatasearch.at.model.LocationModel;
import com.spatialdatasearch.at.persistence.LocationServiceDAO;
import com.spatialdatasearch.at.web.pages.index.BasePage;

public class UploadFilePage extends BasePage {

  private static final long serialVersionUID = 1L;

  @SpringBean
  private LocationServiceDAO locationServiceDAO;

  private final static Logger log = LoggerFactory.getLogger(UploadFilePage.class);
  private FileUploadField fileUpload;

  private String selectedSeparatorType = ",";
  private String selectedColumnType = "[ Discard ]";
  private String selectedMarkerType = "[ Default ]";

  private String sepSelected;
  private String upload_folder;
  private FileUpload uploadedFile = null;
  private String csvSelectedName = "";
  private final List<String> sep = new ArrayList<String>();
  private final List<String> types = new ArrayList<String>();
  private final List<String> markers = new ArrayList<String>();
  private final List<String> select = new ArrayList<String>();
  List<LocationModel> list = new ArrayList<LocationModel>();

  private Integer col = 0;

  public void setFormatLatLng(LocationModel obj, String field) {
    String newFormat = "";
    boolean startCopying = false;
    boolean isLatitudeRead = false;

    for (int i = 0; i < field.length(); ++i) {
      char x = field.charAt(i);
      if (x == '(') {
        startCopying = !startCopying;
      }
      if (x != '(' && startCopying && !isLatitudeRead) {
        newFormat += x;
      }
      if (startCopying && x == ' ') {
        obj.setLongitude(new BigDecimal(new Double(newFormat), new MathContext(
            newFormat.length() - 2)));
        newFormat = "";
        isLatitudeRead = true;
      }
      if (x != ' ' && x != ')' && isLatitudeRead) {
        newFormat += x;
      }
      if (x == ')') {
        obj.setLatitude(new BigDecimal(new Double(newFormat), new MathContext(
            newFormat.length() - 2)));
        startCopying = !startCopying;
      }
    }
  }

  public List<LocationModel> getSavedData() {
    return list;
  }

  public String checkFormat(String field) {

    String newFormat = "";
    boolean startQuotes = false;
    for (int i = 0; i < field.length(); ++i) {
      char x = field.charAt(i);
      if (x == '"') {
        startQuotes = !startQuotes;
      }
      if (startQuotes && (x == ',' || x == ';')) {
        newFormat += "";
      } else {
        newFormat += x;
      }
    }
    return newFormat;
  }

  public void prepareComboBoxes() {
    sep.add(",");
    sep.add(";");
    types.add("[ Discard ]");
    types.add("Name or title");
    types.add("Type or category");
    types.add("Latitude");
    types.add("Longitude");
    types.add("Latitude and longitude");
    types.add("Address");
    types.add("Street");
    types.add("Street number");
    types.add("District or postal code");
    types.add("Website");
    types.add("Description, information or remark");
    types.add("Email");
    types.add("Phone number");
    types.add("Date");
    types.add("Schedule or opening hours");
    Collections.sort(types);
  }

  public void addColumn(String type) {
    switch (type.trim()) {
      case "[ Discard ]":
        log.info("----------------discard");
        select.add(col, "discard");
        break;
      case "Name or title":
        log.info("----------------nombre");
        select.add(col, "name");
        break;
      case "Type or category":
        log.info("----------------type");
        select.add(col, "type");
        break;
      case "Latitude":
        log.info("----------------latitude");
        select.add(col, "latitude");
        break;
      case "Longitude":
        log.info("----------------longitude");
        select.add(col, "longitude");
        break;
      case "Latitude and longitude":
        log.info("----------------latitude&longitude");
        select.add(col, "latitude_longitude");
        break;
      case "Address":
        log.info("----------------address");
        select.add(col, "address");
        break;
      case "Street":
        log.info("----------------street");
        select.add(col, "street");
        break;
      case "Street number":
        log.info("----------------number");
        select.add(col, "number");
        break;
      case "District or postal code":
        log.info("----------------district");
        select.add(col, "district");
        break;
      case "Website":
        log.info("----------------website");
        select.add(col, "website");
        break;
      case "Description, information or remark":
        log.info("----------------description");
        select.add(col, "description");
        break;
      case "Email":
        log.info("----------------email");
        select.add(col, "email");
        break;
      case "Phone number":
        log.info("----------------phone");
        select.add(col, "phone");
        break;
      case "Date":
        log.info("----------------date");
        select.add(col, "date");
        break;
      case "Schedule or opening hours":
        log.info("----------------schedule");
        select.add(col, "schedule");
        break;
      default:
        break;
    }
  }

  @SuppressWarnings("rawtypes")
  public List showSampleCSV() {
    List sampleRows = null;
    try {
      BufferedReader csvFile = null;
      String datasetFile = upload_folder + uploadedFile.getClientFileName();

      csvFile =
          new BufferedReader(new InputStreamReader(new FileInputStream(datasetFile),
              StandardCharsets.ISO_8859_1));
      String line1 = csvFile.readLine();
      String line2 = csvFile.readLine();
      String line3 = csvFile.readLine();
      csvFile.close();
      sampleRows = Arrays.asList(line1, line2, line3);

    } catch (IOException e) {
      log.error("Error while reading from csv " + e.getMessage());
    }
    return sampleRows;
  }

  @SuppressWarnings("unchecked")
  public UploadFilePage(final PageParameters parameters) {
    add(new FeedbackPanel("feedback"));

    final Form<?> formTypes = new Form<Void>("formTypes");
    add(formTypes);

    final Model<String> colId = Model.of("Column \"1\": ");
    final Label colNumber = new Label("colNumber", colId);
    colNumber.setVisible(false);
    colNumber.setOutputMarkupId(true);

    formTypes.add(colNumber);

    prepareComboBoxes();

    final Label fileName = new Label("fileName", "Marker type: ");
    fileName.setVisible(false);
    formTypes.add(fileName);


    final Button saveType = new Button("saveType") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {
        col++;
        log.info("COLUMN " + col);
        colId.setObject("Column \"" + (col + 1) + "\" : ");
        log.info("SAVETYPE IS  " + col);
      }
    };
    formTypes.add(saveType);
    saveType.setVisible(false);

    markers.add("[ Default ]");
    markers.add("Attractions");
    markers.add("Top places");
    markers.add("Markets");
    markers.add("Christmas markets");
    markers.add("Parks");
    markers.add("City bike");
    markers.add("City walks");
    markers.add("Donau leisure venues");
    markers.add("Natural monuments");
    markers.add("Swimming places");
    markers.add("Police");
    markers.add("Camping");
    Collections.sort(markers);

    final DropDownChoice<String> csvName =
        new DropDownChoice<String>("csvName",
            new PropertyModel<String>(this, "selectedMarkerType"), markers) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void onBeforeRender() {
            super.onBeforeRender();
            csvSelectedName = selectedMarkerType;
            log.info("Your csv name 1.." + selectedMarkerType);

          }

          @Override
          protected void onSelectionChanged(String newSelection) {
            csvSelectedName = newSelection;
            log.info("Your csv name 2.." + newSelection);
          }

          @Override
          protected boolean wantOnSelectionChangedNotifications() {
            return true;
          }
        };
    csvName.setVisible(false);
    formTypes.add(csvName);


    final Button saveCSV = new Button("saveCSV") {
      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {
        if (uploadedFile != null) {

          String field = "";
          String line = null;
          String datasetFile = upload_folder + uploadedFile.getClientFileName();
          BufferedReader csvFile = null;
          try {
            csvFile =
                new BufferedReader(new InputStreamReader(new FileInputStream(datasetFile),
                    StandardCharsets.ISO_8859_1));
            csvFile.readLine();

            while ((line = csvFile.readLine()) != null) {
              log.info("old format " + line);
              line = checkFormat(line);
              log.info("new format " + line);
              log.info("separator..." + sepSelected);
              String[] att = line.split(sepSelected);
              log.info("columns... " + att.length);
              LocationModel obj = new LocationModel();
              obj.setCsvName(csvSelectedName);
              int i = 0;
              int ncol = 0;
              while (ncol < att.length) {
                field = att[ncol];
                log.info("Attribute: " + field);
                String sel = select.get(i);
                log.info("Selection: " + sel);
                switch (sel) {
                  case "discard":
                    break;
                  case "name":
                    obj.setName(field);
                    break;
                  case "type":
                    if (field.equals("")) {
                      obj.setType(null);
                    } else {
                      obj.setType(field);
                    }
                    break;
                  case "latitude":
                    obj.setLatitude(new BigDecimal(new Double(field.replace(",", ".")),
                        new MathContext(field.length() - 1)));
                    break;
                  case "longitude":
                    obj.setLongitude(new BigDecimal(new Double(field.replace(",", ".")),
                        new MathContext(field.length() - 2)));
                    break;
                  case "latitude_longitude":
                    setFormatLatLng(obj, field);
                    break;
                  case "address":
                    if (field.equals("")) {
                      obj.setAddress(null);
                    } else {
                      obj.setAddress(field);
                    }
                    break;
                  case "street":
                    if (field.equals("")) {
                      obj.setStreet(null);
                    } else {
                      obj.setStreet(field);
                    }
                    break;
                  case "number":
                    if (field.equals("")) {
                      obj.setNumber(null);
                    } else {
                      obj.setNumber(field);
                    }
                    break;
                  case "district":
                    if (field.equals("")) {
                      obj.setDistrict(null);
                    } else {
                      obj.setDistrict(field);
                    }
                    break;
                  case "website":
                    if (field.equals("")) {
                      obj.setWebsite(null);
                    } else {
                      obj.setWebsite(field);
                    }
                    break;
                  case "description":
                    if (field.equals("")) {
                      obj.setDescription(null);
                    } else {
                      obj.setDescription(field);
                    }
                  case "email":
                    if (field.equals("")) {
                      obj.setEmail(null);
                    } else {
                      obj.setEmail(field);
                    }
                    break;
                  case "phone":
                    if (field.equals("")) {
                      obj.setPhone(null);
                    } else {
                      obj.setPhone(field);
                    }
                    break;
                  case "date":
                    if (field.equals("")) {
                      obj.setDate(null);
                    } else {
                      obj.setDate(field);
                    }
                  case "schedule":
                    if (field.equals("")) {
                      obj.setSchedule(null);
                    } else {
                      obj.setSchedule(field);
                    }
                  default:
                    break;
                }
                i++;
                ncol++;
              }
              log.info(obj.getName());
              log.info(obj.getAddress());
              log.info(obj.getWebsite());
              list.add(obj);
            }
            csvFile.close();

          } catch (FileNotFoundException e) {

            e.printStackTrace();

          } catch (IOException e) {

            e.printStackTrace();
          }
        }
        for (LocationModel l : list) {
          log.info(l.getName());
          log.info(l.getLatitude().toString());
          log.info(l.getLongitude().toString());
          log.info(l.getAddress());
        }
        if (!list.isEmpty()) {
          for (LocationModel l : list) {
            log.info(l.getName());
            log.info(l.getAddress());
            log.info(l.getWebsite());
          }
          locationServiceDAO.storeLocationModel(list);
          list.clear();
          list = locationServiceDAO.readLocationModel();
          info("Thanks! Your CSV file \"" + uploadedFile.getClientFileName()
              + "\" was correctly upload.");

          formTypes.setVisible(false);
          // fileName.setVisible(false);
          // csvName.setVisible(false);
          // //saveFileName.setVisible(true);
          // //saveCSV.setVisible(false);
          // //separatorAtt.setVisible(true);
          // typeAtt.setVisible(false);
          // saveType.setVisible(false);
          // colNumber.setVisible(false);
        }
      }
    };
    saveCSV.setVisible(false);
    formTypes.add(saveCSV);

    final DropDownChoice<String> typeAtt =
        new DropDownChoice<String>("types", new PropertyModel<String>(this, "selectedColumnType"),
            types) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void onBeforeRender() {
            super.onBeforeRender();
            log.info("Your selection is 1.." + selectedColumnType);
            addColumn(selectedColumnType);

          }

          @Override
          protected void onSelectionChanged(String newSelection) {

            log.info("Your selection is 2.." + newSelection);
            addColumn(newSelection);

          }

          @Override
          protected boolean wantOnSelectionChangedNotifications() {
            return true;
          }
        };

    typeAtt.setVisible(false);
    formTypes.add(typeAtt);

    final Label sepType = new Label("sepType", "Separator type: ");
    sepType.setVisible(false);
    formTypes.add(sepType);

    final DropDownChoice<String> separatorAtt =
        new DropDownChoice<String>("separatorType", new PropertyModel<String>(this,
            "selectedSeparatorType"), sep) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void onBeforeRender() {
            super.onBeforeRender();
            sepSelected = selectedSeparatorType;
            log.info("SEPARATOR 1..." + selectedSeparatorType);

          }

          @Override
          protected void onSelectionChanged(String newSelection) {
            log.info("SEPARATOR 2..." + newSelection);
            sepSelected = newSelection;
          }

          @Override
          protected boolean wantOnSelectionChangedNotifications() {
            log.info("CHANGING SEP");
            return true;
          }

        };

    separatorAtt.setVisible(false);
    formTypes.add(separatorAtt);

    final Button saveSeparator = new Button("saveSeparator") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {
        super.onSubmit();
        Component setVisible2 = colNumber.setVisible(true);
        Component setVisible3 = typeAtt.setVisible(true);
        Component setVisible = saveType.setVisible(true);
        saveCSV.setVisible(true);
      }
    };
    formTypes.add(saveSeparator);
    saveSeparator.setVisible(false);

    final Button saveFileName = new Button("saveFileName") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {

        super.onSubmit();
        sepType.setVisible(true);
        separatorAtt.setVisible(true);
        saveSeparator.setVisible(true);
        info("Markers with name: \"" + csvSelectedName + "\"");
      }
    };

    formTypes.add(saveFileName);
    saveFileName.setVisible(false);

    List<?> sampleRows = Arrays.asList();
    @SuppressWarnings("rawtypes")
    final PropertyListView rowsCsv = new PropertyListView("listview", sampleRows) {

      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem item) {
        item.add(new Label("rowNum", item.getIndex()));
        item.add(new Label("data", item.getModel()));
      }
    };
    rowsCsv.setVisible(false);
    add(rowsCsv);

    final Label exampleRowsLabel =
        new Label("exampleRowsLabel", "Here you can see the first 3 rows of the CSV file: ");
    exampleRowsLabel.setVisible(false);
    add(exampleRowsLabel);

    Form<?> form = new Form<Void>("form") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onSubmit() {

      }
    };

    form.setMultiPart(true); // need for uploads
    form.setMaxSize(Bytes.megabytes(10)); // max upload size 10mb
    form.add(fileUpload = new FileUploadField("fileUpload"));
    add(form);

    final Label selFile = new Label("selFile", "Select file: ");
    form.add(selFile);

    Button uploadFile = new Button("uploadFile") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {
        ServletContext servletContext = WebApplication.get().getServletContext();
        upload_folder = servletContext.getRealPath("/") + "files/";
        log.info(" DIRECTORIO " + upload_folder);

        uploadedFile = fileUpload.getFileUpload();
        if (uploadedFile != null) {

          // write to a new file
          File newFile = new File(upload_folder + uploadedFile.getClientFileName());

          if (newFile.exists()) {
            newFile.delete();
          }

          try {
            newFile.createNewFile();
            uploadedFile.writeTo(newFile);
            info("Saved file: \"" + uploadedFile.getClientFileName() + "\"");
          } catch (IOException e) {
            e.printStackTrace();
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }


          List<String> sampleRows = showSampleCSV();
          if (sampleRows != null) {
            exampleRowsLabel.setVisible(true);
            rowsCsv.setList(sampleRows);
            rowsCsv.setVisible(true);
            fileUpload.setVisible(false);
            selFile.setVisible(false);
            Component setVisible = this.setVisible(false);
          }

          fileName.setVisible(true);
          csvName.setVisible(true);
          saveFileName.setVisible(true);
        }
      }
    };

    form.add(uploadFile);

    // fileName.setVisible(true);
    // csvName.setVisible(true);
    // saveFileName.setVisible(true);
    // saveCSV.setVisible(true);
    // separatorAtt.setVisible(true);
    // typeAtt.setVisible(true);
    // colNumber.setVisible(true);

  }
}
