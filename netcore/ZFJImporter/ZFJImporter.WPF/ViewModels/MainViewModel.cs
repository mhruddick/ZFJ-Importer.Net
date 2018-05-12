using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ZFJImporter.WPF
{
    public class MainViewModel : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;

        private string serverUrl;

        public string ServerUrl
        {
            get { return serverUrl; }
            set
            {
                if (serverUrl != value)
                {
                    serverUrl = value;
                    PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(ServerUrl)));
                }
            }
        }

    }
}