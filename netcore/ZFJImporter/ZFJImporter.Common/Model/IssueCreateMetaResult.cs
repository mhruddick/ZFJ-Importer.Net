using System.Collections.Generic;
using System.Runtime.Serialization;

namespace ZFJImporter.Common.Model
{
    [DataContract]
    public class IssueCreateMetaResult
    {
        [DataMember(Name = "projects")]
        public IEnumerable<Project> Projects { get; set; }
    }
}