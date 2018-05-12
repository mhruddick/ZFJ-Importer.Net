using System.Runtime.Serialization;

namespace ZFJImporter.Common.Model
{
    [DataContract]
    public class Project
    {
        [DataMember(Name = "id")]
        public int Id { get; set; }

        [DataMember(Name = "name")]
        public string Name { get; set; }

        [DataMember(Name = "key")]
        public string Key { get; set; }
    }
}