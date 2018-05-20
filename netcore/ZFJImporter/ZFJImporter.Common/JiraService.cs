using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
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

        public async Task<IEnumerable<IssueType>> GetProjectIssueTypesAsync(int projectId)
        {
            string requestUri = $"rest/api/latest/issue/createmeta?projectIds={projectId}&expand=projects.issuetypes.fields";

            var createMetaResponse = await GetAsAsync<IssueCreateMetaResult>(requestUri);

            var issueTypes = createMetaResponse?.Projects?.SingleOrDefault()?.IssueTypes;

            PopulateFields(issueTypes);

            return issueTypes;
        }

        private void PopulateFields(IEnumerable<IssueType> issueTypes)
        {
            foreach (var issueType in issueTypes)
            {
                var fields = new List<Field>();

                foreach (var kvp in issueType.RawJsonFields)
                {
                    var jsonString = kvp.Value.ToString();

                    var field = JsonConvert.DeserializeObject<Field>(jsonString);
                    field.Id = kvp.Key;
                    fields.Add(field);
                }

                issueType.Fields = fields;
            }
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