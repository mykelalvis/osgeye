package org.osgeye.console.commands.diagnosis;

import org.osgeye.domain.Bundle;
import org.osgeye.domain.ExportedPackage;
import org.osgeye.domain.ImportedPackage;

public class UsesConflict
{
  public final Bundle importBundle;
  
  public final ImportedPackage exportedImport;

  public final ImportedPackage usesConflictImport;

  public final Bundle exportBundle;
  
  public final ExportedPackage exportedPackage;
  
  public final ExportedPackage usesWiredExport;

  public UsesConflict(Bundle importBundle, ImportedPackage exportedImport, ImportedPackage usesConflictImport,
      Bundle exportBundle, ExportedPackage exportedPackage, ExportedPackage usesWiredExport)
  {
    this.importBundle = importBundle;
    this.exportedImport = exportedImport;
    this.usesConflictImport = usesConflictImport;
    this.exportBundle = exportBundle;
    this.exportedPackage = exportedPackage;
    this.usesWiredExport = usesWiredExport;
  }
}
