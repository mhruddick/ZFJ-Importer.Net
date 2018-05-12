using System;
using System.Threading.Tasks;
using ZFJImporter.Common;

namespace ZFJImporter
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Hello World!");

            Run().Wait();
        }

        public static async Task Run()
        {
            Console.WriteLine("Getting projects...");

            var service = new JiraService("server", "username", "password");

            var projects = await service.GetProjects();

            foreach (var project in projects)
            {
                Console.WriteLine($"Id: {project.Id}  Name: {project.Name}  Key: {project.Key}");
            }
            

            Console.ReadKey();
        }
    }
}