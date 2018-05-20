using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.Runtime.CompilerServices;
using System.Windows;
using ZFJImporter.Common;
using ZFJImporter.Common.Model;

namespace ZFJImporter.WPF
{
    public class MainViewModel : INotifyPropertyChanged
    {
        public MainViewModel(Func<string> getPasswordDelegate)
        {
            LoginCommand = new DelegateCommand(OnLogin, CanLogin);

            _getPasswordDelegate = getPasswordDelegate;

            ServerUrl = ConfigurationManager.AppSettings["defaultServer"];
            Username = ConfigurationManager.AppSettings["defaultUser"];
        }

        public event PropertyChangedEventHandler PropertyChanged;

        private Func<string> _getPasswordDelegate;
        private IJiraService _service;
        private string _serverUrl;
        private string _username;
        private IEnumerable<Project> _projects;

        #region LoginCommand

        public DelegateCommand LoginCommand { get; set; }

        private void OnLogin()
        {
            InitializeService();
        }

        private bool CanLogin(object parameter)
        {
            bool validServerUrl =
                !string.IsNullOrWhiteSpace(ServerUrl) &&
                Uri.IsWellFormedUriString(ServerUrl, UriKind.Absolute) &&
                ServerUrl.StartsWith("http", StringComparison.InvariantCultureIgnoreCase);

            return
                validServerUrl &&
                !string.IsNullOrWhiteSpace(Username) &&
                !string.IsNullOrWhiteSpace(_getPasswordDelegate?.Invoke());
        }

        public void RaiseCanLoginChanged()
        {
            LoginCommand.RaiseCanExecuteChanged();
        }

        #endregion
        
        public string ServerUrl
        {
            get { return _serverUrl; }
            set
            {
                if (UpdateValue(ref _serverUrl, value))
                {
                    RaiseCanLoginChanged();
                }
            }
        }
        
        public string Username
        {
            get { return _username; }
            set
            {
                if (UpdateValue(ref _username, value))
                {
                    RaiseCanLoginChanged();
                }
            }
        }
        
        public IEnumerable<Project> Projects
        {
            get { return _projects; }
            set { UpdateValue(ref _projects, value); }
        }

        private void InitializeService()
        {
            try
            {
                _service = new JiraService(ServerUrl, Username, _getPasswordDelegate?.Invoke());
            }
            catch (Exception ex)
            {
                MessageBox.Show($"JIRA service initialization failed. \r\n\r\n{ex.Message}");
                return;
            }

            GetProjects();
        }

        private async void GetProjects()
        {
            try
            {
                Projects = await _service.GetProjectsAsync();
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Failed to load projects. \r\n\r\n{ex.Message}");
            }
        }

        private bool UpdateValue<T>(ref T storage, T value, [CallerMemberName] string propertyName = null)
        {
            if (!Equals(storage, value))
            {
                storage = value;
                PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));

                return true;
            }

            return false;
        }
    }
}