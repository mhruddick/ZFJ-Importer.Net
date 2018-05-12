using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Threading.Tasks;
using ZFJImporter.Common.Model;

namespace ZFJImporter.Common
{
    public class JiraService : IJiraService
    {
        private HttpClient client;

        public JiraService(string serverUrl, string username, string password)
        {
            client = new HttpClient();
            client.BaseAddress = new Uri(serverUrl);
            client.DefaultRequestHeaders.Add("User-Agent", "ZFJImporter");
            client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", Convert.ToBase64String(Encoding.ASCII.GetBytes($"{username}:{password}")));
        }

        public async Task<IEnumerable<Project>> GetProjects()
        {
            var serializer = new DataContractJsonSerializer(typeof(IEnumerable<Project>));
            var stream = await client.GetStreamAsync("/rest/api/latest/project");

            return (serializer.ReadObject(stream) as IEnumerable<Project>).ToList();
        }

        public async Task<IEnumerable<Issue>> GetProjectIssues(int projectId)
        {
            var serializer = new DataContractJsonSerializer(typeof(IEnumerable<Issue>));
            var stream = await client.GetStreamAsync($"rest/api/latest/issue/createmeta?projectIds={projectId}&expand=projects.issuetypes.fields");

            return (serializer.ReadObject(stream) as IEnumerable<Issue>).ToList();
        }
    }
}