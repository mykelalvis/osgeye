package org.osgeye.console.commands.diagnosis;

import org.osgeye.domain.Bundle;
import org.osgeye.domain.ExportedPackage;
import org.osgeye.domain.manifest.ExportPackagesDeclaration;
import org.osgeye.domain.manifest.ImportPackagesDeclaration;
import org.osgeye.utils.Pair;

public class UsesConflict
{
  public final Bundle importBundle;
  
  public final Pair<ImportPackagesDeclaration, String> exportedImport;

  public final Pair<ImportPackagesDeclaration, String> usesConflictImport;

  public final Bundle exportBundle;
  
  public final ExportedPackage exportedPackage;
  
  public final ExportedPackage usesWiredExport;

  public UsesConflict(Bundle importBundle, Pair<ImportPackagesDeclaration, String> exportedImport, Pair<ImportPackagesDeclaration, String> usesConflictImport,
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
