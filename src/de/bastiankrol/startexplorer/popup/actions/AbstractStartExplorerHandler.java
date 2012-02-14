package de.bastiankrol.startexplorer.popup.actions;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

import de.bastiankrol.startexplorer.Activator;
import de.bastiankrol.startexplorer.RuntimeExecCalls;
import de.bastiankrol.startexplorer.customcommands.CommandConfig;
import de.bastiankrol.startexplorer.preferences.PreferenceUtil;
import de.bastiankrol.startexplorer.util.PathChecker;
import de.bastiankrol.startexplorer.util.PathChecker.ResourceType;

/**
 * Common base class for all handlers of this plug-in.
 */
public abstract class AbstractStartExplorerHandler extends AbstractHandler
{

  private RuntimeExecCalls runtimeExecCalls;
  private PathChecker pathChecker;
  private PreferenceUtil preferenceUtil;

  /**
   * Constructor
   */
  AbstractStartExplorerHandler()
  {
    this.runtimeExecCalls = Activator.getDefault().getRuntimeExecCalls();
    this.pathChecker = Activator.getDefault().getPathChecker();
    this.preferenceUtil =  new PreferenceUtil();
  }

  /**
   * Returns the RuntimeExecCalls instance.
   */
  RuntimeExecCalls getRuntimeExecCalls()
  {
    return this.runtimeExecCalls;
  }

  /**
   * Returns the PathChecker instance.
   */
  PathChecker getPathChecker()
  {
    return this.pathChecker;
  }
  
  PreferenceUtil getPreferenceUtil()
  {
    return this.preferenceUtil;
  }

  File resourceToFile(IResource resource, ResourceType resourceType,
      ExecutionEvent event) throws ExecutionException
  {
    IPath path = resource.getLocation();
    if (path == null)
    {
      Activator.logMessage(IStatus.WARNING,
          "Current selection contains a resource object with null-location: "
              + resource);
      return null;
    }
    String pathString = path.toOSString();
    File file = this.getPathChecker()
        .checkPath(pathString, resourceType, event);
    return file;
  }

  static interface CommandRetriever
  {
    Command getCommandFromConfig(CommandConfig commandConfig);
  }

  @SuppressWarnings("unchecked")
  protected static <T extends AbstractStartExplorerHandler> T getCorrespondingHandlerForCustomCommand(
      CommandConfig commandConfig, CommandRetriever commandRetriever)
  {
    if (commandConfig != null)
    {
      Command correspondingEclipseCommand = commandRetriever
          .getCommandFromConfig(commandConfig);
      if (correspondingEclipseCommand != null)
      {
        IHandler handler = correspondingEclipseCommand.getHandler();
        if (handler instanceof AbstractStartFromResourceHandler
            || handler instanceof AbstractStartFromStringHandler)
        {
          return (T) handler;
        }
      }
    }
    return null;
  }
}
