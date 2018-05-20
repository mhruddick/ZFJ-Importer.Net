using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

namespace ZFJImporter.Common.Model
{
    [DataContract]
    public class Field
    {
        public string Id { get; set; }
        
        [DataMember(Name = "name")]
        public string Name { get; set; }

        [DataMember(Name = "required")]
        public bool Required { get; set; }
    }
}