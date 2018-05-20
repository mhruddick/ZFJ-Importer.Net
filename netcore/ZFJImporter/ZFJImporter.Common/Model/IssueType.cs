using Newtonsoft.Json.Linq;
using System.Collections.Generic;
using System.Runtime.Serialization;

namespace ZFJImporter.Common.Model
{
    [DataContract]
    public class IssueType
    {
        [DataMember(Name = "id")]
        public int Id { get; set; }

        [DataMember(Name = "name")]
        public string Name { get; set; }

        [DataMember(Name = "fields")]
        public JObject RawJsonFields { get; set; }

        public IEnumerable<Field> Fields { get; set; }
    }
}