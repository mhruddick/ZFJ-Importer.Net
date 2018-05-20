using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.Diagnostics;
using System.Linq;
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
        private IEnumerable<IssueType> _issueTypes;
        private IEnumerable<Field> _fields;
        private int _selectedProjectId = -1;
        private int _selectedIssueTypeId = -1;

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
            set
            {
                if (UpdateValue(ref _projects, value))
                {
                    TrySelectDefaultProject();
                }
            }
        }

        public int SelectedProjectId
        {
            get { return _selectedProjectId; }
            set
            {
                if (UpdateValue(ref _selectedProjectId, value))
                {
                    Debug.WriteLine($"Selected project ID changed to {SelectedProjectId}");
                    GetProjectIssueTypesAsync(SelectedProjectId);
                }
            }
        }

        public IEnumerable<IssueType> IssueTypes
        {
            get { return _issueTypes; }
            set
            {
                if (UpdateValue(ref _issueTypes, value))
                {
                    TrySelectDefaultIssueType();
                }
            }
        }

        public int SelectedIssueTypeId
        {
            get { return _selectedIssueTypeId; }
            set
            {
                if (UpdateValue(ref _selectedIssueTypeId, value))
                {
                    Debug.WriteLine($"Selected issue type ID changed to {SelectedIssueTypeId}");
                    Fields = IssueTypes.Single(i => i.Id == SelectedIssueTypeId).Fields.OrderByDescending(f => f.Required).ThenBy(f => f.Name).ToList();
                }
            }
        }

        public IEnumerable<Field> Fields
        {
            get { return _fields; }
            set { UpdateValue(ref _fields, value); }
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

            GetProjectsAsync();
        }

        private void TrySelectDefaultProject()
        {
            if (Projects == null) return;

            var defaultProjectName = ConfigurationManager.AppSettings["defaultProject"];

            if (string.IsNullOrWhiteSpace(defaultProjectName)) return;

            var defaultProject = Projects.FirstOrDefault(p => string.Compare(p.Name, defaultProjectName, true) == 0);

            if (defaultProject != null)
            {
                SelectedProjectId = defaultProject.Id;
            }
        }

        private void TrySelectDefaultIssueType()
        {
            if (IssueTypes == null) return;

            var defaultIssueTypeName = ConfigurationManager.AppSettings["defaultIssueType"];

            if (string.IsNullOrWhiteSpace(defaultIssueTypeName)) return;

            var defaultIssueType = IssueTypes.FirstOrDefault(p => string.Compare(p.Name, defaultIssueTypeName, true) == 0);

            if (defaultIssueType != null)
            {
                SelectedIssueTypeId = defaultIssueType.Id;
            }
        }

        private async void GetProjectsAsync()
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

        private async void GetProjectIssueTypesAsync(int projectId)
        {
            try
            {
                IssueTypes = await _service.GetProjectIssueTypesAsync(projectId);
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Failed to load issue types for project ID {projectId}. \r\n\r\n{ex.Message}");
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