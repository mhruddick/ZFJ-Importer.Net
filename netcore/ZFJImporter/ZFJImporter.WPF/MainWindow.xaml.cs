using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using ZFJImporter.Common;

namespace ZFJImporter.WPF
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private IJiraService service;


        public MainViewModel ViewModel
        {
            get { return (MainViewModel)DataContext; }
            set { DataContext = value; }
        }

        public MainWindow()
        {
            InitializeComponent();

            ViewModel = new MainViewModel();
        }

        private void LoginButton_Click(object sender, RoutedEventArgs e)
        {
            service = new JiraService(ServerField.Text, UserNameField.Text, UserPasswordField.Password);
            GetProjects();
        }

        private async void GetProjects()
        {
            var projects = await service.GetProjects();

            ProjectsListView.ItemsSource = projects;
        }
    }
}