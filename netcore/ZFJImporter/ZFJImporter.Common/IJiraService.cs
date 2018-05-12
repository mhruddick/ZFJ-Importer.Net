using System.Collections.Generic;
using System.Threading.Tasks;
using ZFJImporter.Common.Model;

namespace ZFJImporter.Common
{
    public interface IJiraService
    {
        Task<IEnumerable<Issue>> GetProjectIssues(int projectId);
        Task<IEnumerable<Project>> GetProjects();
    }
}