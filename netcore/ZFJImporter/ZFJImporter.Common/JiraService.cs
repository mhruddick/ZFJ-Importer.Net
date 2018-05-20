using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Headers;
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

        public async Task<IEnumerable<Project>> GetProjectsAsync()
        {
            string requestUri = "/rest/api/latest/project";

            return await GetAsAsync<IEnumerable<Project>>(requestUri);
        }

        public async Task<IEnumerable<Issue>> GetProjectIssuesAsync(int projectId)
        {
            string requestUri = $"rest/api/latest/issue/createmeta?projectIds={projectId}&expand=projects.issuetypes.fields";

            return await GetAsAsync<IEnumerable<Issue>>(requestUri);
        }

        private async Task<T> GetAsAsync<T>(string requestUri)
        {
            HttpResponseMessage response = await client.GetAsync(requestUri);

            if (!response.IsSuccessStatusCode)
            {
                throw new HttpRequestException($"The HTTP request to '{requestUri}' failed with status '{response.StatusCode}'.");
            }

            return await response.Content.ReadAsAsync<T>();
        }
    }
}