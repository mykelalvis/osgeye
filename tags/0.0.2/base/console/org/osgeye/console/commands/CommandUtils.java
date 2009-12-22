package org.osgeye.console.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import jline.ConsoleReader;

import org.osgeye.console.BundleStore;
import org.osgeye.domain.Bundle;
import org.osgeye.domain.BundleState;
import org.osgeye.domain.Version;
import org.osgeye.domain.VersionRange;

public class CommandUtils
{
  static public BundleStore bundleStore;
  
  static public ConsoleReader reader;
  
  static public CommandPrinter printer = new CommandPrinter();
  
  static public void assertMinLength(List<String> subcommands, int length) throws InvalidCommandException
  {
    if (subcommands.size() < length)
    {
      throw new InvalidCommandException("Expected more subcommands.");
    }
  }
  
  static public void assertEmpty(List<String> subcommands) throws InvalidCommandException
  {
    if (subcommands.size() > 0)
    {
      throw new InvalidCommandException("Unexpected arguments " + drain(subcommands));
    }
  }
  
  static public void assertLength(List<String> subcommands, int length) throws InvalidCommandException
  {
    if (subcommands.size() < length)
    {
      throw new InvalidCommandException("Expected more subcommands");
    }
    else if (subcommands.size() > length)
    {
      throw new InvalidCommandException("Expected less subcommands");
    }
  }
  
  static public void assertMaxLength(List<String> subcommands, int length) throws InvalidCommandException
  {
    if (subcommands.size() > length)
    {
      throw new InvalidCommandException("Expected less subcommands.");
    }
  }
  
  static public void assertMinMaxLength(List<String> subcommands, int minLength, int maxLength) throws InvalidCommandException
  {
    assertMinLength(subcommands, minLength);
    assertMaxLength(subcommands, maxLength);
  }
  
  static public String assertNextValue(List<String> subcommands, String... validValues) throws InvalidCommandException
  {
    return assertValue(subcommands, 0, validValues);
  }

  static public String assertValue(List<String> subcommands, int index, String... validValues) throws InvalidCommandException
  {
    
    assertMinLength(subcommands, (index + 1));
    String nextEnteredValue = subcommands.remove(index);
    for (String validValue : validValues)
    {
      if (validValue.equalsIgnoreCase(validValue))
      {
        return nextEnteredValue;
      }
    }
    
    String errorMsg = "Invalid subcommand " + nextEnteredValue + " must be one of:";
    for (String validValue : validValues)
    {
      errorMsg += "\n    " + validValue;
    }
    
    throw new InvalidCommandException(errorMsg);
  }
  
  static public void assertAllValues(List<String> subcommands, String... validValues) throws InvalidCommandException
  {
    START_LOOP: for (String subcommand : subcommands)
    {
      for (String validValue : validValues)
      {
        if (validValue.equalsIgnoreCase(subcommand))
        {
          continue START_LOOP;
        }
      }
      
      String errorMsg = "Invalid subcommand " + subcommand + " must be one of:";
      for (String validValue : validValues)
      {
        errorMsg += "\n    " + validValue;
      }
      
      throw new InvalidCommandException(errorMsg);
    }
  }
  
  static public String drain(List<String> subcommands)
  {
    return drain(subcommands, 0);
  }

  static public String drain(List<String> subcommands, int fromIndex)
  {
    String text = "";
    for (int i = (subcommands.size() - 1); i >= fromIndex; i--)
    {
      text = subcommands.remove(i) + " " + text;
    }
    return text.trim();
  }
  
  static public VersionRange parseVersionRange(List<String> subcommands) throws InvalidCommandException
  {
    String versionRangeText = subcommands.remove(0);
    
    if (versionRangeText.equals("*"))
    {
      return new VersionRange(new Version(0, 0, 0));
    }
    else
    {
      char startChar = versionRangeText.charAt(0);
      try
      {
        if ((startChar == '(') || (startChar == '['))
        {
          char endChar = versionRangeText.charAt(versionRangeText.length() - 1);
          if ((endChar != ')') && (startChar != ']'))
          {
            assertMinLength(subcommands, 1);
            versionRangeText += " " + subcommands.remove(0);
          }
          
          return new VersionRange(versionRangeText);
        }
        else
        {
          Version version = new Version(versionRangeText);
          Version noQualifier = new Version(version.getMajor(), version.getMinor(), version.getMicro());
          return new VersionRange(noQualifier, true, version, true);
        }
      }
      catch (IllegalArgumentException iaexc)
      {
        throw new InvalidCommandException("Invalid version or version range '" + versionRangeText + "'");
      }
    }
  }
  
  static public List<Bundle> findMatchingBundles(List<String> subcommands, String action, boolean displayMatchedBundles, boolean requireConfirmation, List<BundleState> states) throws InvalidCommandException
  {
    String bundleNamePattern = (subcommands.size() > 0) ? subcommands.remove(0) : ".*";
    if (bundleNamePattern.equals("*")) bundleNamePattern = ".*";
    
    VersionRange versionRange = null;
    if (subcommands.size() > 0)
    {
      versionRange = parseVersionRange(subcommands);
    }
    
    List<Bundle> matchingBundles = new ArrayList<Bundle>();
    List<Bundle> allBundles = bundleStore.getBundles();
    for (Bundle bundle : allBundles)
    {
      try
      {
        if (bundle.getSymbolicName().matches(bundleNamePattern) 
            && ((versionRange == null) || (versionRange.isWithinRange(bundle.getVersion())))
            && ((states == null) || (states.size() == 0) || (states.contains(bundle.getState()))))
        {
          matchingBundles.add(bundle);
        }
      }
      catch (PatternSyntaxException psexc)
      {
        throw new InvalidCommandException("Invalid regular expression '" + bundleNamePattern + "' for bundle name.");
      }
    }
    
    if (matchingBundles.size() == 0)
    {
      printer.println("No bundles currently match the given name pattern.");
      return null;
    }
    else
    {
      if (displayMatchedBundles)
      {
        printer.println("The following bundles will be acted on:");
        printer.pushIndent();
        for (Bundle bundle : matchingBundles)
        {
          printer.println(bundle);
        }
        printer.popIndent();
      }
      
      if (requireConfirmation && !confirmation("Confirm " + action + " on the above bundles"))
      {
        return null;
      }
      else
      {
        return matchingBundles;
      }
    }
  }
  
  static public boolean confirmation(String confirmText) throws InvalidCommandException
  {
    try
    {
      String confirm = reader.readLine("\n" + confirmText + " (N) ");
      
      if ("Y".equalsIgnoreCase(confirm) || "YES".equalsIgnoreCase(confirm))
      {
        return true;
      }
      else
      {
        printer.println("Command aborted.");
        return false;
      }
    }
    catch (IOException ioexc)
    {
      throw new InvalidCommandException("Unable to get user confirmation due to " + ioexc.getMessage());
    }
  }
  
  static public List<Long> toBundleIds(List<Bundle> bundles)
  {
    List<Long> bundleIds = new ArrayList<Long>();
    for (Bundle bundle : bundles)
    {
      bundleIds.add(bundle.getId());
    }
    return bundleIds;
  }
  
}
