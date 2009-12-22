package org.osgeye.domain.manifest;

import static org.osgeye.utils.UtilityMethods.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgeye.domain.Version;

public class Manifest implements Serializable
{
  static private final long serialVersionUID = 8664003503709463751L;

  /**
   * The Bundle-ActivationPolicy specifies how the framework should activate 
   * the bundle once started. See Activation Policies
   */
  static public final String ACTIVATION_POLICY = "Bundle-ActivationPolicy";
  
  /**
   * The Bundle-Activator header specifies the name of the class used to start and 
   * stop the bundle.
   */
  static public final String ACTIVATOR = "Bundle-Activator";
  
  /**
   * The Bundle-Category header holds a comma-separated list of category 
   * names.
   */
  static public final String CATEGORY = "Bundle-Category";
  
  /**
   * The Bundle-Classpath header defines a comma-separated list of JAR file path 
   * names or directories (inside the bundle) containing classes and resources. 
   * The period (’.’) specifies the root directory of the bundle’s JAR. The period is 
   * also the default.
   */
  static public final String CLASSPATH = "Bundle-Classpath";
  
  /**
   * The Bundle-ContactAddress header provides the contact address of the vendor. 
   */
  static public final String CONTACT_ADRESS = "Bundle-ContactAddress";

  /**
   * The Bundle-Copyright header contains the copyright specification for this 
   * bundle. 
   */
  static public final String COPYRIGHT = "Bundle-Copyright";
  
  /**
   * The Bundle-Description header defines a short description of this bundle. 
   */
  static public final String DESCRIPTION = "Bundle-Description";
  
  /**
   * The Bundle-DocURL headers must contain a URL pointing to documentation 
   * about this bundle.
   */
  static public final String DOC_URL = "Bundle-DocURL";
  
  /**
   * The Bundle-Localization header contains the location in the bundle where 
   * localization files can be found. The default value is OSGI-INF/l10n/bundle. 
   * Translations are by default therefore OSGI-INF/l10n/bundle_de.properties, 
   * OSGI-INF/l10n/bundle_nl.properties, etc.
   */
  static public final String LOCALIZATION = "Bundle-Localization";

  /**
   * The Bundle-ManifestVersion header defines that the bundle follows the 
   * rules of this specification. The Bundle-ManifestVersion header determines 
   * whether the bundle follows the rules of this specification. It is 1 (the 
   * default) for Release 3 Bundles, 2 for Release 4 and later. Future version of the 
   * OSGi Service Platform can define higher numbers for this header.
   */
  static public final String MANIFEST_VERSION = "Bundle-ManifestVersion";

  /**
   * The Bundle-Name header defines a readable name for this bundle. This 
   * should be a short, human-readable name that can contain spaces.
   */
  static public final String NAME = "Bundle-Name";

  /**
   * The Bundle-NativeCode header contains a specification of native code 
   * libraries contained in this bundle.
   */
  static public final String NATIVE_CODE = "Bundle-NativeCode";

  /**
   * he Bundle-RequiredExecutionEnvironment contains a comma-separated 
   * list of execution environments that must be present on the Service Platform.
   */
  static public final String REQUIRED_EXECUTION_ENVIRONMENT = "Bundle-RequiredExecutionEnvironment";

  /**
   * The Bundle-SymbolicName header specifies a unique, non-localizable name 
   * for this bundle. This name should be based on the reverse domain name 
   * convention. Required.
   */
  static public final String SYMBOLIC_NAME = "Bundle-SymbolicName";

  /**
   * The Bundle-UpdateLocation header specifies a URL where an update for this 
   * bundle should come from. If the bundle is updated, this location should be 
   * used, if present, to retrieve the updated JAR file.
   */
  static public final String UPDATE_LOCATION = "Bundle-UpdateLocation";

  /**
   * The Bundle-Vendor header contains a human-readable description of the 
   * bundle vendor. 
   */
  static public final String VENDOR = "Bundle-Vendor";

  /**
   * The Bundle-Version header specifies the version of this bundle.
   */
  static public final String VERSION = "Bundle-Version";

  /**
   * The DynamicImport-Package header contains a comma-separated list of 
   * package names that should be dynamically imported when needed.
   */
  static public final String DYNAMIC_IMPORT_PACKAGE = "DynamicImport-Package";

  /**
   * The Export-Package header contains a declaration of exported packages.
   */
  static public final String EXPORT_PACKAGE = "Export-Package";

  /**
   * @deprecated
   */
  static public final String EXPORT_SERVICE = "Export-Service";

  /**
   * The Fragment-Host header defines the host bundle for this fragment.
   */
  static public final String FRAGMENT_HOST = "Fragment-Host";

  /**
   * The Import-Package header declares the imported packages for this bundle.
   */
  static public final String IMPORT_PACKAGE = "Import-Package";

  /**
   * @deprecated
   */
  static public final String IMPORT_SERVICE = "Import-Service";
  
  /**
   * The Require-Bundle header specifies the required exports from another bundle.
   */
  static public final String REQUIRE_BUNDLE = "Require-Bundle";
  
  private String manifestFile;
  private List<String> orderedHeaderKeys;
  private Map<String, String> headerProperties;
  
  private transient String name;
  private transient Version version;
  private transient Integer manifestVersion;
  private transient String activator;
  private transient List<String> categories;
  private transient List<ExportPackagesDeclaration> exportDeclarations;
  private transient List<ImportPackagesDeclaration> importDeclarations;
  private transient String nativeCode;
  private transient String requiredExecutionEnvironment;
  private transient String symbolicName;
  private transient String updateLocation;
  private transient String vendor;
  private transient List<String> dynamicImportPackages;
  private transient FragmentHost fragmentHost;
  private transient RequireBundle requireBundle;
  private transient String prettyManifestFile;
  
  
  public Manifest(String manifestFile)
  {
    this.manifestFile = manifestFile;
    
    orderedHeaderKeys = new ArrayList<String>();
    headerProperties = new HashMap<String, String>();
    
    String[] lines = manifestFile.split("\n");
    
    String currentName = null;
    String currentValue = null;
    for (String line : lines)
    {
      if (line.trim().length() == 0) continue;
      
      if (line.startsWith(" "))
      {
        currentValue += line.trim();
      }
      else
      {
        if (currentName != null)
        {
          orderedHeaderKeys.add(currentName);
          headerProperties.put(currentName, currentValue);
        }
        
        int index = line.indexOf(':');
        if (index == -1)
        {
          System.out.println(line);
        }
        currentName = line.substring(0, index);
        if (index == (line.length() - 1))
        {
          currentValue = "";
        }
        else
        {
          currentValue = line.substring((index + 1), line.length()).trim();
        }
      }
    }

    if (currentName != null)
    {
      orderedHeaderKeys.add(currentName);
      headerProperties.put(currentName, currentValue);
    }
    
  }
  
  public String getHeaderValue(String headerName)
  {
    return headerProperties.get(headerName);
  }
  
  public String getName()
  {
    if ((name == null) && headerProperties.containsKey(NAME))
    {
      name = headerProperties.get(NAME);
    }
    return name;
  }

  public Version getVersion()
  {
    if (version == null)
    {
      version = headerProperties.containsKey(VERSION) ? new Version(headerProperties.get(VERSION)) : new Version();      
    }
    return version;
  }

  public int getManifestVersion()
  {
    if (manifestVersion == null)
    {
      manifestVersion = headerProperties.containsKey(MANIFEST_VERSION) ? 
          Integer.parseInt(headerProperties.get(MANIFEST_VERSION)) : 2;      
    }
    return manifestVersion;
  }

  public String getActivator()
  {
    if ((activator == null) && headerProperties.containsKey(ACTIVATOR))
    {
      activator = headerProperties.get(ACTIVATOR);
    }
    return activator;
  }
  
  public List<String> getCategories()
  {
    if (categories == null)
    {
      if (headerProperties.containsKey(CATEGORY))
      {
        categories = Arrays.asList(headerProperties.get(CATEGORY).split(","));
      }
      else
      {
        categories = new ArrayList<String>();
      }      
    }
    return categories;
  }

  public List<ImportPackagesDeclaration> getImportDeclarations()
  {
    if (importDeclarations == null)
    {
      importDeclarations = new ArrayList<ImportPackagesDeclaration>();
      if (headerProperties.containsKey(IMPORT_PACKAGE))
      {
        List<String> declarations = parseDeclarations(headerProperties.get(IMPORT_PACKAGE));
        for (String declaration : declarations)
        {
          importDeclarations.add(new ImportPackagesDeclaration(declaration));
        }
      }      
    }
    return importDeclarations;
  }

  public List<ExportPackagesDeclaration> getExportDeclarations()
  {
    if (exportDeclarations == null)
    {
      exportDeclarations = new ArrayList<ExportPackagesDeclaration>();
      if (headerProperties.containsKey(EXPORT_PACKAGE))
      {
        List<String> declarations = parseDeclarations(headerProperties.get(EXPORT_PACKAGE));
        for (String declaration : declarations)
        {
          exportDeclarations.add(new ExportPackagesDeclaration(declaration));
        }
      }      
    }
    return exportDeclarations;
  }
  
  public String getNativeCode()
  {
    if ((nativeCode == null) && headerProperties.containsKey(NATIVE_CODE))
    {
      nativeCode = headerProperties.get(NATIVE_CODE);
    }
    return nativeCode;
  }

  public String getRequiredExecutionEnvironment()
  {
    if ((requiredExecutionEnvironment == null) && headerProperties.containsKey(REQUIRED_EXECUTION_ENVIRONMENT))
    {
      requiredExecutionEnvironment = headerProperties.get(REQUIRED_EXECUTION_ENVIRONMENT);
    }
    return requiredExecutionEnvironment;
  }

  public String getUpdateLocation()
  {
    if ((updateLocation == null) && headerProperties.containsKey(UPDATE_LOCATION))
    {
      updateLocation = headerProperties.get(UPDATE_LOCATION);
    }
    return updateLocation;
  }

  public String getSymbolicName()
  {
    if ((symbolicName == null) && (headerProperties.containsKey(SYMBOLIC_NAME)))
    {
      symbolicName = headerProperties.get(SYMBOLIC_NAME);
    }
    return symbolicName;
  }

  public String getVendor()
  {
    if ((vendor == null) || (headerProperties.containsKey(VENDOR)))
    {
      vendor = headerProperties.get(VENDOR);
    }
    return vendor;
  }

  public List<String> getDynamicImportPackages()
  {
    if (dynamicImportPackages == null)
    {
      if (headerProperties.containsKey(DYNAMIC_IMPORT_PACKAGE))
      {
        dynamicImportPackages = Arrays.asList(headerProperties.get(DYNAMIC_IMPORT_PACKAGE).split(","));
      }
      else
      {
        dynamicImportPackages = new ArrayList<String>();
      }      
    }
    return dynamicImportPackages;
  }

  public FragmentHost getFragmentHost()
  {
    if ((fragmentHost == null) && headerProperties.containsKey(FRAGMENT_HOST))
    {
      fragmentHost = new FragmentHost(headerProperties.get(FRAGMENT_HOST));
    }
    return fragmentHost;
  }

  public RequireBundle getRequireBundle()
  {
    if ((requireBundle == null) && headerProperties.containsKey(REQUIRE_BUNDLE))
    {
      requireBundle = new RequireBundle(headerProperties.get(REQUIRE_BUNDLE));
    }
    return requireBundle;
  }

  protected List<String> parseDeclarations(String text)
  {
    List<String> declarations = new ArrayList<String>();
    
    while (text.length() > 0)
    {
      int nextDeclarationCommon = findNextDeclarationComman(text);
      if (nextDeclarationCommon == -1)
      {
        declarations.add(text);
        text = "";
      }
      else
      {
        declarations.add(text.substring(0, nextDeclarationCommon));
        text = text.substring((nextDeclarationCommon + 1), text.length());
      }
    }
    return declarations;
  }
  
  private int findNextDeclarationComman(String text)
  {
    int index = text.indexOf(',');
    if (index == -1)
    {
      return -1;
    }
    else
    {
      String declaration = text.substring(0, index);
      while ((countChars('"', declaration) % 2) != 0)
      {
        index = text.indexOf(',', (index + 1));
        if (index == -1)
        {
          return -1; // Assume that the last delcartion ends with one or more attributes with quotes.
        }
        declaration = text.substring(0, index);
      }
      return index;
    }
  }
  
  public String toString()
  {
    return symbolicName + ";version=\"" + version + "\"";
  }
  
  public String getFile()
  {
    return manifestFile;
  }
  
  public String getPrettyFile()
  {
   if (prettyManifestFile == null)
   {
     prettyManifestFile = "";
     for (int i = 0; i < orderedHeaderKeys.size(); i++)
     {
       if (i != 0) prettyManifestFile += "\n";
       
       String headerKey = orderedHeaderKeys.get(i);
       
       if (headerKey.equals(IMPORT_PACKAGE))
       {
         List<ImportPackagesDeclaration> imports = getImportDeclarations();
         if ((imports != null) && (imports.size() > 0))
         {
           prettyManifestFile += "Import-Package:";
           for (int j = 0; j < imports.size(); j++)
           {
             prettyManifestFile += "\n    " + imports.get(j);
           }
         }
       }
       else if (headerKey.equals(EXPORT_PACKAGE))
       {
         List<ExportPackagesDeclaration> exports = getExportDeclarations();
         if ((exports != null) && (exports.size() > 0))
         {
           prettyManifestFile += "Export-Package:";
           for (int j = 0; j < exports.size(); j++)
           {
             prettyManifestFile += "\n    " + exports.get(j);
           }
         }
       }
       else
       {
         String headerValue = headerProperties.get(headerKey);
         if (headerValue.contains(","))
         {
           String[] subValues = headerValue.split(",");
           prettyManifestFile += headerKey + ":";
           for (int j = 0; j < subValues.length; j++)
           {
             prettyManifestFile += "\n    " + subValues[j];
           }
         }
         else
         {
           prettyManifestFile += headerKey + ": " + headerProperties.get(headerKey);
         }
       }
     }
   }
   
   return prettyManifestFile;
  }
}
