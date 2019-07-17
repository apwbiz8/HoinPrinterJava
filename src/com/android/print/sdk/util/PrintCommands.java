package com.android.print.sdk.util;

public class PrintCommands
{
  public static byte[] horizontalTab() { return new byte[] { 9 }; }

  public static byte[] printLineFeed() { return new byte[] { 10 }; }
  
  public static byte[] printCarriageReturn() { return new byte[] { 13 }; }
  
  public static byte[] printReturnStandardMode() { return new byte[] { 12 }; }
  
  public static byte[] cancelPrintData() { return new byte[] { 24 }; }
  
  public static byte[] realTimeStatusTransmission(int n) { return new byte[] { 16, 4, (byte)n }; }
  
  public static byte[] realTimeRequestToPrinter(int n) { return new byte[] { 16, 5, (byte)n }; }
  
  public static byte[] generatePulseAtRealTime(int n, int m, int t) { return new byte[] { 16, 20, (byte)n, (byte)m, (byte)t }; }
  
  public static byte[] printData() { return new byte[] { 27, 12 }; }
  
  public static byte[] setRightSideCharacterSpacing(int n) { return new byte[] { 27, 32, (byte)n }; }
  
  public static byte[] selectPrintMode(int n) { return new byte[] { 27, 33, (byte)n }; }
  
  public static byte[] setAbsolutePrintPosition(int nL, int nH) { return new byte[] { 27, 36, (byte)nL, (byte)nH }; }
  
  public static byte[] cancelUserDefinedCharacterSet() { return new byte[] { 27, 37, 0 }; }
  
  public static byte[] selectUserDefinedCharacterSet() { return new byte[] { 27, 37, 1 }; }
  
  public static byte[] defineUserDefinedCharacters(int c1, int c2, byte[] dots) {
    byte[] part = { 27, 38, 3, (byte)c1, (byte)c2 };
    byte[] destination = new byte[part.length + dots.length];
    System.arraycopy(part, 0, destination, 0, part.length);
    System.arraycopy(dots, 0, destination, part.length, dots.length);
    return destination;
  }
  
  public static byte[] selectBitImageMode(int m, int nL, int nH, byte[] image) {
    byte[] part = { 27, 42, (byte)m, (byte)nL, (byte)nH };
    byte[] destination = new byte[part.length + image.length];
    System.arraycopy(part, 0, destination, 0, part.length);
    System.arraycopy(image, 0, destination, part.length, image.length);
    return destination;
  }
  
  public static byte[] turnUnderlineModeOff() { return new byte[] { 27, 45, 0 }; }
  
  public static byte[] turnUnderlineMode(int n) { return new byte[] { 27, 45, 1 }; }
  
  public static byte[] selectDefaultLineSpacing() { return new byte[] { 27, 50 }; }
  
  public static byte[] setLineSpacing(int n) { return new byte[] { 27, 51, (byte)n }; }
  
  public static byte[] setPeripheralDevice(int n) { return new byte[] { 27, 61, (byte)n }; }
  
  public static byte[] cancelUserDefinedCharacters(int n) { return new byte[] { 27, 63, (byte)n }; }
  
  public static byte[] initializePrinter() { return new byte[] { 27, 64 }; }
  
  public static byte[] setHorizontalTabPositions(byte[] nk) {
    byte[] part = { 27, 68 };
    byte[] destination = new byte[part.length + nk.length + 1];
    System.arraycopy(part, 0, destination, 0, part.length);
    System.arraycopy(nk, 0, destination, part.length, nk.length);
    destination[part.length + nk.length] = 0;
    return destination;
  }
  
  public static byte[] turnOffEmphasizedMode() { return new byte[] { 27, 69, 0 }; }
  
  public static byte[] turnOnEmphasizedMode() { return new byte[] { 27, 69, 1 }; }
  
  public static byte[] turnDoubleStrikeMode(int n) { return new byte[] { 27, 71, (byte)n }; }
  
  public static byte[] printFeedPaper(int n) { return new byte[] { 27, 74, (byte)n }; }
  
  public static byte[] selectPageMode() { return new byte[] { 27, 76 }; }
  
  public static byte[] selectCharacterFont(int n) { return new byte[] { 27, 77, (byte)n }; }
  
  public static byte[] selectAnInternationalCharacterSet(int n) { return new byte[] { 27, 82, (byte)n }; }
  
  public static byte[] selectStandardMode() { return new byte[] { 27, 83 }; }
  
  public static byte[] selectPrintDirectionInPageMode(int n) { return new byte[] { 27, 84, (byte)n }; }
  
  public static byte[] turn90ClockwiseRotationMode(int n) { return new byte[] { 27, 86, (byte)n }; }
  
  public static byte[] setPrintingAreaInPageMode(int xL, int xH, int yL, int yH, int dxL, int dxH, int dyL, int dyH) { return new byte[] { 27, 87, (byte)xL, (byte)xH, (byte)yL, (byte)yH, (byte)dxL, (byte)dxH, (byte)dyL, (byte)dyH }; }
  
  public static byte[] setRelativePrintPosition(int nL, int nH) { return new byte[] { 27, 92, (byte)nL, (byte)nH }; }
  
  public static byte[] selectJustification(int n) { return new byte[] { 27, 97, (byte)n }; }
  
  public static byte[] selectPaperSensorToOutputPaperEndSignals(int n) { return new byte[] { 27, 99, 51, (byte)n }; }
  
  public static byte[] selectPaperSensorToStopPrinting(int n) { return new byte[] { 27, 99, 52, (byte)n }; }
  
  public static byte[] disablePanelButtons() { return new byte[] { 27, 99, 53, 0 }; }
  
  public static byte[] enablePanelButtons() { return new byte[] { 27, 99, 53, 1 }; }
  
  public static byte[] printFeedNLines(int n) { return new byte[] { 27, 100, (byte)n }; }
  
  public static byte[] executePaperFullCut() { return new byte[] { 27, 105 }; }
  
  public static byte[] executePaperPartialCut() { return new byte[] { 27, 109 }; }
  
  public static byte[] generatePulse(int m, int t1, int t2) { return new byte[] { 27, 112, (byte)m, (byte)t1, (byte)t2 }; }
  
  public static byte[] selectCharacterCodeTable(int n) { return new byte[] { 27, 116, (byte)n }; }
  
  public static byte[] turnsOffUpsideDownPrintingMode() { return new byte[] { 27, 123, 0 }; }

  public static byte[] turnsOnUpsideDownPrintingMode() { return new byte[] { 27, 123, 1 }; }
  
  public static byte[] printNVBitImage(int n, int m) { return new byte[] { 28, 112, (byte)n, (byte)m }; }
  
  public static byte[] defineNVBitImage(int n, byte[] image) {
    byte[] part = { 28, 113, (byte)n };
    byte[] destination = new byte[part.length + image.length];
    System.arraycopy(part, 0, destination, 0, part.length);
    System.arraycopy(image, 0, destination, part.length, image.length);
    return destination;
  }
  
  public static byte[] selectCharacterSize(int n) { return new byte[] { 29, 33, (byte)n }; }
  
  public static byte[] setAbsoluteVerticalPrintPositionInPageMade(int nL, int nH) { return new byte[] { 29, 36, (byte)nL, (byte)nH }; }
  
  public static byte[] defineDownloadedBitImage(int x, int y, byte[] image) {
    byte[] part = { 29, 42, (byte)x, (byte)y };
    byte[] destination = new byte[part.length + image.length];
    System.arraycopy(part, 0, destination, 0, part.length);
    System.arraycopy(image, 0, destination, part.length, image.length);
    return destination;
  }
  
  public static byte[] printDownloadedBitImage(int m) { return new byte[] { 29, 47, (byte)m }; }
  
  public static byte[] startOrEndMacroDefinition() { return new byte[] { 29, 58 }; }
  
  public static byte[] turnOffWhiteBlackReversePrintingMode() { return new byte[] { 29, 66, 0 }; }
  
  public static byte[] turnOnWhiteBlackReversePrintingMode() { return new byte[] { 29, 66, 1 }; }
  
  public static byte[] selectPrintingPositionOfHRICharacters(int n) { return new byte[] { 29, 72, (byte)n }; }
  
  public static byte[] setLeftMargin(int nL, int nH) { return new byte[] { 29, 76, (byte)nL, (byte)nH }; }
  
  public static byte[] setHorizontalAndVerticalMotionUnits(int x, int y) { return new byte[] { 29, 80, (byte)x, (byte)y }; }
  
  public static byte[] selectCutModeAndCutPaper(int m, int n) {
    if (m == 66) {
      return new byte[] { 29, 86, 66, (byte)n };
    }
    return new byte[] { 29, 86, (byte)m };
  }

  public static byte[] setPrintingAreaWidth(int nL, int nH) { return new byte[] { 29, 87, (byte)nL, (byte)nH }; }
  
  public static byte[] setRelativeVerticalPrintPositionInPageMode(int nL, int nH) { return new byte[] { 29, 92, (byte)nL, (byte)nH }; }
  
  public static byte[] executeMacro(int r, int t, int m) { return new byte[] { 29, 94, (byte)r, (byte)t, (byte)m }; }
  
  public static byte[] setAutomaticStatusBack(int n) { return new byte[] { 29, 97, (byte)n }; }
  
  public static byte[] selectFontForHumanReadableInterpretationCharacters(int n) { return new byte[] { 29, 102, (byte)n }; }
  
  public static byte[] selectBarCodeHeight(int n) { return new byte[] { 29, 104, (byte)n }; }

  public static byte[] printBarCode(int m, int n, byte[] data) {
    if (m <= 6) {
      byte[] part = { 29, 107, (byte)m };
      byte[] destination = new byte[part.length + data.length + 1];
      System.arraycopy(part, 0, destination, 0, part.length);
      System.arraycopy(data, 0, destination, part.length, data.length);
      destination[part.length + data.length] = 0;
      return destination;
    } 
    byte[] part = { 29, 107, (byte)m, (byte)n };
    byte[] destination = new byte[part.length + data.length];
    System.arraycopy(part, 0, destination, 0, part.length);
    System.arraycopy(data, 0, destination, part.length, data.length);
    return destination;
  }
  
  public static byte[] transmitStatus(int n) { return new byte[] { 29, 114, (byte)n }; }
  
  public static byte[] printRasterBitImage(int m, int xL, int xH, int yL, int yH, byte[] image) {
    byte[] part = { 29, 118, 48, (byte)m, (byte)xL, (byte)xH, (byte)yL, (byte)yH };
    byte[] destination = new byte[part.length + image.length];
    System.arraycopy(part, 0, destination, 0, part.length);
    System.arraycopy(image, 0, destination, part.length, image.length);
    return destination;
  }
 
  public static byte[] setBarCodeWidth(int n) { return new byte[] { 29, 119, (byte)n }; }
  
  public static byte[] selectQRCodeModel(int n1, int n2) { return new byte[] { 29, 40, 107, 4, 0, 49, 65, (byte)n1, (byte)n2 }; }
  
  public static byte[] setQRCodeSizeOfModule(int n) { return new byte[] { 29, 40, 107, 3, 0, 49, 67, (byte)n }; }
  
  public static byte[] selectQRCodeErrorCorrectionLevel(int n) { return new byte[] { 29, 40, 107, 3, 0, 49, 69, (byte)n }; }
  
  public static byte[] storeQRCodeDataInTheSymbolStorageArea(int pL, int pH, byte[] data) {
    byte[] part = { 29, 40, 107, (byte)pL, (byte)pH, 49, 80, 48 };
    byte[] destination = new byte[part.length + data.length];
    System.arraycopy(part, 0, destination, 0, part.length);
    System.arraycopy(data, 0, destination, part.length, data.length);
    return destination;
  }

  public static byte[] printQRCodeSymbolDataInTheSymbolStorageArea() { return new byte[] { 29, 40, 107, 3, 0, 49, 81, 48 }; }
  
  public static byte[] setNumberOfColumnsOfTheDataAreaForPDF417(int n) { return new byte[] { 29, 40, 107, 3, 0, 48, 65, (byte)n }; }
  
  public static byte[] setNumberOfRowsOfDataAreaForPDF417(int n) { return new byte[] { 29, 40, 107, 3, 0, 48, 66, (byte)n }; }
  
  public static byte[] setModuleWidthOfOnePDF417SymbolDots(int n) { return new byte[] { 29, 40, 107, 3, 0, 48, 67, (byte)n }; }
  
  public static byte[] setPDF417ModuleHeight(int n) { return new byte[] { 29, 40, 107, 3, 0, 48, 68, (byte)n }; }
  
  public static byte[] setErrorCorrectionLevelForPDF417Symbols(int m, int n) { return new byte[] { 29, 40, 107, 4, 0, 48, 69, (byte)m, (byte)n }; }
  
  public static byte[] storeSymbolDataInThePDF417SymbolStorageArea(int pL, int pH, byte[] data) {
    byte[] part = { 29, 40, 107, (byte)pL, (byte)pH, 48, 80, 48 };
    byte[] destination = new byte[part.length + data.length];
    System.arraycopy(part, 0, destination, 0, part.length);
    System.arraycopy(data, 0, destination, part.length, data.length);
    return destination;
  }
  
  public static byte[] printPDF417SymbolDataInTheSymbolStorageArea() { return new byte[] { 29, 40, 107, 4, 0, 48, 81, 48 }; }
}
